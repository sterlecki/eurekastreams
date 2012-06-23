#start setting stuff up here
#need to install apache, if it isn't already there

#setting the global path for all exec's 

Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

exec{ "update apt packages":
	command => "apt-get update",
}

#Dev Resources
package{ "git-core":
	ensure => present,
	require => Exec["update apt packages"],
}

package{ "maven2":
	ensure => present,
	require => Exec["update apt packages"],
	notify => File["/home/vagrant/.m2/settings.xml"]
}

file{ "/home/vagrant/.m2/":
	ensure => directory,
	owner => "vagrant",
	group => "vagrant",
}
	
file{ "/home/vagrant/.m2/settings.xml":
    ensure => present,
    source => "puppet:///modules/dev/settings.xml",
    require => [Package["maven2"], File["/home/vagrant/.m2"]],
    #notify => Exec["build eurekastreams"],
}


#Build eurekastreams source with settings applicable to this env.

#Application Resources
package{ "tomcat6":
	ensure => present,
	require => Exec["update apt packages"],
}

service{ "tomcat6":
	ensure => running,
	require => Package["tomcat6"],
}

package{ "postgresql":
	ensure => present,
	require => Exec["update apt packages"],
	notify => Exec["create eurekastreams user"]
}

package{ "memcached":
	ensure => present,
	require => Exec["update apt packages"],
}

package{ "apache2":
	ensure => present,
	require => Exec["update apt packages"],
	notify => Exec["enable mod_authnz_ldap for apache2"]
}

package{ "libapache2-mod-auth-kerb":
	ensure => present,
	require => Package["apache2"],
}

exec{ "enable mod_authnz_ldap for apache2":
	command => 'sudo ln -s /etc/apache2/mods-available/authnz_ldap.load /etc/apache2/mods-enabled/authnz_ldap.load',
	require => Package["apache2"],
	refreshonly => true
}

exec{ "create eurekastreams db":
	command => "sudo -u postgres createdb -O eurekastreams eurekastreams",
	require => Exec["create eurekastreams user"],
	refreshonly => true,
}

exec{ "create eurekastreams user":
	command => 'sudo -u postgres psql -c "create user eurekastreams with password \'eurekastreams\';" postgres',
	logoutput => true,
	require => Package["postgresql"],
	refreshonly => true,
	notify => Exec["create eurekastreams db"]
}