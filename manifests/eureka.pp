#start setting stuff up here
#need to install apache, if it isn't already there

#setting the global path for all exec's 

Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

exec{ "update apt packages":
	command => "apt-get update",
}

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
	require => Exec["update apt packages"]
}

package{ "memcached":
	ensure => present,
	require => Exec["update apt packages"],
}

package{ "apache2":
	ensure => present,
	require => Exec["update apt packages"],
}

package{ "libapache2-mod-auth-kerb":
	ensure => present,
	require => Package["apache2"],
}

exec{ "enable mod_authnz_ldap for apache2":
	command => 'sudo ln -s /etc/apache2/mods-available/authnz_ldap.load /etc/apache2/mods-enabled/authnz_ldap.load',
	require => Package["apache2"],
}

exec{ "create eurekstreams db":
	command => "sudo -u postgres createdb -O eurekastreams eurekastreams",
	require => Exec["create eurekastreams user"]
}

exec{ "create eurekastreams user":
	command => 'sudo -u postgres psql -c "create user eurekastreams with password \'eurekastreams\';" postgres',
	logoutput => true,
	require => Package["postgresql"],
}