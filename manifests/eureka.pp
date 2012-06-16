#start setting stuff up here
#need to install apache, if it isn't already there

exec{ "update apt packages":
	command => "apt-get update",
}

package{ "tomcat6":
	ensure => present,
	require => "update apt packages"
}

service{ "tomcat6":
	ensure => running,
	require => Package["tomcat6"],
}

package{ "postgresql":
	ensure => present,
	require => "update apt packages"
}

exec{ "create eurekstreams db":
	command => "sudo -u postgres createdb -O eurekastreams eurekastreams",
	require => Exec["create eurekastreams user"]
}

exec{ "create eurekastreams user":
	command => 'sudo -u postgres psql -c "create user eurekastreams with password 'eurekastreams';" postgres'
	logoutput => true,
	require => Package["postgresql"],
}