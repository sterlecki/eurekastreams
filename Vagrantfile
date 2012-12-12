# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |onebox|
  onebox.vm.box = "precise64"
  onebox.vm.box_url = "http://files.vagrantup.com/precise64.box"
  onebox.vm.host_name = "web"
  onebox.vm.customize ["modifyvm", :id, "--memory", 8192]
  onebox.vm.provision :puppet, :options => "--verbose --debug" do |puppet|
    puppet.manifests_path = "manifests"
    puppet.manifest_file = "eureka.pp"
    puppet.module_path = "modules"
  end
end
