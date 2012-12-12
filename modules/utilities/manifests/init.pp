class utilities {
}
define check_line ($file,
    $line,
    $ensure = 'present') {
    case $ensure {
        default : {
            err("unknown ensure value ${ensure}")
        }
        present : {
            exec {
                "/bin/echo '${line}' >> '${file}'" :
                    unless => "/bin/grep -qFx '${line}' '${file}'"
            }
        }
        absent : {
            exec {
                "/bin/grep -vFx '${line}' '${file}' | /usr/bin/tee '${file}' > /dev/null 2>&1" :
                    onlyif => "/bin/grep -qFx '${line}' '${file}'"
            }

            # Use this resource instead if your platform's grep doesn't support -vFx;
            # note that this command has been known to have problems with lines containing quotes.
            # exec { "/usr/bin/perl -ni -e 'print unless /^\\Q${line}\\E\$/' '${file}'":
            #     onlyif => "/bin/grep -qFx '${line}' '${file}'"
            # }

        }
    }
}
define prepend_if_no_such_line ($file,
    $line,
    $refreshonly = false) {
    exec {
        "/usr/bin/perl -p0i -e 's/^/$line\n/;' '$file'" :
            unless => "/bin/grep -Fxqe '$line' '$file'",
            path => "/bin",
            refreshonly => $refreshonly,
    }
}
# useful for checking if a config section doesn't exist before adding it
define prepend_line_if_no_such_line ($file,
    $newline,
    $checkline,
    $refreshonly = false) {
    exec {
        "/usr/bin/perl -p0i -e 's/^/$newline\n/;' '$file'" :
            unless => "/bin/grep -Fxqe '$checkline' '$file'",
            path => "/bin",
            refreshonly => $refreshonly,
    }
}
define delete_lines ($file,
    $pattern) {
    exec {
        "/bin/sed -i -r -e '/$pattern/d' $file" :
            onlyif => "/bin/grep -E '$pattern' '$file'",
    }
}