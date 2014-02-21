
set_defaults() {
    SERVERNAME="localhost"
    PORT="5432"
    DATABASE="engine"
    USERNAME="engine"
    VERBOSE=false
    LOGFILE="$(basename "$0").log"

    if [ -n "${ENGINE_PGPASS}" ]; then
        export PGPASSFILE="${ENGINE_PGPASS}"
        unset PGPASSWORD
    else
        export PGPASSFILE="/etc/ovirt-engine/.pgpass"
        if [ ! -r "${PGPASSFILE}" ]; then
            export PGPASSFILE="${HOME}/.pgpass"
        fi
    fi
}

