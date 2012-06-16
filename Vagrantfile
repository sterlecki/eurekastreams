# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant::Config.run do |onebox|
  onebox.vm.box = "base"
  onebox.vm.host_name = "web"
  onebox.vm.provision :puppet, :options => "--verbose --debug" do |puppet|
    puppet.manifests_path = "manifests"
    puppet.manifest_file = "eureka.pp"
  end
end