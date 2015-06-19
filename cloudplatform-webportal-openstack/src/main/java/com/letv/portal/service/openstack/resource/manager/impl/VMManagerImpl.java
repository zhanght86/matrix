package com.letv.portal.service.openstack.resource.manager.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.slf4j.Logger;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.letv.portal.service.openstack.exception.APINotAvailableException;
import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.exception.RegionNotFoundException;
import com.letv.portal.service.openstack.exception.ResourceNotFoundException;
import com.letv.portal.service.openstack.exception.VMDeleteException;
import com.letv.portal.service.openstack.resource.FlavorResource;
import com.letv.portal.service.openstack.resource.NetworkResource;
import com.letv.portal.service.openstack.resource.VMResource;
import com.letv.portal.service.openstack.resource.impl.FlavorResourceImpl;
import com.letv.portal.service.openstack.resource.impl.VMResourceImpl;
import com.letv.portal.service.openstack.resource.manager.ImageManager;
import com.letv.portal.service.openstack.resource.manager.NetworkManager;
import com.letv.portal.service.openstack.resource.manager.VMCreateConf;
import com.letv.portal.service.openstack.resource.manager.VMManager;

public class VMManagerImpl extends AbstractResourceManager implements VMManager {

	private static final Logger logger = org.slf4j.LoggerFactory
			.getLogger(VMManager.class);

	private NovaApi novaApi;

	private ImageManager imageManager;

	private NetworkManager networkManager;

	public VMManagerImpl(String endpoint, String userId, String password) {
		super(endpoint, userId, password);

		Iterable<Module> modules = ImmutableSet
				.<Module> of(new SLF4JLoggingModule());

		novaApi = ContextBuilder.newBuilder("openstack-nova")
				.endpoint(endpoint)
				.credentials(userId + ":" + userId, password).modules(modules)
				.buildApi(NovaApi.class);
	}

	@Override
	public void close() throws IOException {
		novaApi.close();
	}

	@Override
	public Set<String> getRegions() {
		return novaApi.getConfiguredRegions();
	}

	@Override
	public List<VMResource> list(String region) throws RegionNotFoundException,
			ResourceNotFoundException {
		checkRegion(region);

		ServerApi serverApi = novaApi.getServerApi(region);
		List<Server> resources = serverApi.listInDetail().concat().toList();
		List<VMResource> vmResources = new ArrayList<VMResource>(
				resources.size());
		for (Server resource : resources) {
			vmResources.add(new VMResourceImpl(region, resource, this,
					imageManager));
		}
		return vmResources;
	}

	@Override
	public VMResource get(String region, String id)
			throws RegionNotFoundException, ResourceNotFoundException {
		checkRegion(region);

		ServerApi serverApi = novaApi.getServerApi(region);
		Server server = serverApi.get(id);
		if (server != null) {
			return new VMResourceImpl(region, server, this, imageManager);
		} else {
			throw new ResourceNotFoundException(MessageFormat.format(
					"VM \"{0}\" is not found.", id));
		}
	}

	@Override
	public VMResource create(String region, VMCreateConf conf)
			throws OpenStackException {
		checkRegion(region);

		CreateServerOptions createServerOptions = new CreateServerOptions();

		List<NetworkResource> networkResources = conf.getNetworkResources();
		if (networkResources != null) {
			Set<String> networks = new HashSet<String>(
					networkResources.size());
			for (int i = 0; i < networkResources.size(); i++) {
				networks.add(networkResources.get(i).getId());
			}

			// test code begin(ssh login)
			{
				NetworkManagerImpl networkManagerImpl = (NetworkManagerImpl) networkManager;
				NeutronApi neutronApi = networkManagerImpl.getNeutronApi();
				NetworkApi networkApi = neutronApi.getNetworkApi(region);
				for (Network network : networkApi.list().concat().toList()) {
					if("__user_private_network".equals(network.getName())){
						networks.add(network.getId());
						break;
					}
				}
			}
			// test code end

			createServerOptions.networks(networks);
		}

		String adminPass = conf.getAdminPass();
		if (adminPass != null) {
			createServerOptions.adminPass(adminPass);
		}

		// test code begin(ssh login)
		{
			// Optional<SecurityGroupApi> securityGroupApi = novaApi
			// .getSecurityGroupApi(region);
			// if (securityGroupApi.isPresent()) {
			// SecurityGroup defaultSecurityGroup = null;
			// List<SecurityGroup> securityGroups = securityGroupApi.get()
			// .list().toList();
			// for (SecurityGroup securityGroup : securityGroups) {
			// if ("default".equals(securityGroup.getName())) {
			// defaultSecurityGroup = securityGroup;
			// break;
			// }
			// }
			// if (defaultSecurityGroup != null) {
			// } else {
			// throw new
			// OpenStackException("Default security group is not found.");
			// }
			// } else {
			// throw new APINotAvailableException(SecurityGroupApi.class);
			// }

			createServerOptions.securityGroupNames("default");
		}
		// test code end

		ServerApi serverApi = novaApi.getServerApi(region);
		ServerCreated serverCreated = serverApi.create(conf.getName(), conf
				.getImageResource().getId(), conf.getFlavorResource().getId(),
				createServerOptions);
		Server server = serverApi.get(serverCreated.getId());

		// test code begin(ssh login)
		{
			Optional<FloatingIPApi> floatingIPApiOptional = novaApi
					.getFloatingIPApi(region);
			if (!floatingIPApiOptional.isPresent()) {
				throw new APINotAvailableException(FloatingIPApi.class);
			}
			FloatingIPApi floatingIPApi = floatingIPApiOptional.get();
			FloatingIP floatingIP = floatingIPApi.create();
			floatingIPApi.addToServer(floatingIP.getIp(), server.getId());
		}
		// test code end

		return new VMResourceImpl(region, server, this, imageManager);
	}

	@Override
	public void delete(String region, VMResource vm)
			throws RegionNotFoundException, VMDeleteException {
		checkRegion(region);

		ServerApi serverApi = novaApi.getServerApi(region);
		boolean isSuccess = serverApi.delete(vm.getId());
		if (!isSuccess) {
			throw new VMDeleteException(MessageFormat.format(
					"VM \"{0}\" delete failed.", vm.getId()));
		}
	}

	@Override
	public void start(String region, VMResource vm)
			throws RegionNotFoundException {
		checkRegion(region);

		ServerApi serverApi = novaApi.getServerApi(region);
		serverApi.start(vm.getId());
	}

	@Override
	public void stop(String region, VMResource vm)
			throws RegionNotFoundException {
		checkRegion(region);

		ServerApi serverApi = novaApi.getServerApi(region);
		serverApi.stop(vm.getId());
	}

	@Override
	public List<FlavorResource> listFlavorResources(String region)
			throws RegionNotFoundException {
		checkRegion(region);

		FlavorApi flavorApi = novaApi.getFlavorApi(region);
		List<Flavor> resources = flavorApi.listInDetail().concat().toList();
		List<FlavorResource> flavorResources = new ArrayList<FlavorResource>(
				resources.size());
		for (Flavor resource : resources) {
			flavorResources.add(new FlavorResourceImpl(region, resource));
		}
		return flavorResources;
	}

	@Override
	public FlavorResource getFlavorResource(String region, String id)
			throws RegionNotFoundException, ResourceNotFoundException {
		checkRegion(region);

		FlavorApi flavorApi = novaApi.getFlavorApi(region);
		Flavor flavor = flavorApi.get(id);
		if (flavor != null) {
			return new FlavorResourceImpl(region, flavor);
		} else {
			throw new ResourceNotFoundException(MessageFormat.format(
					"Flavor \"{0}\" is not found.", id));
		}
	}

	public void setImageManager(ImageManager imageManager) {
		this.imageManager = imageManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

}
