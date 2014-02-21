#!/bin/sh
###############################################################################################################
# The purpose of this utility is to display and clean asynchronous tasks and corresponding
# Job steps/Compensation data.
# The utility enables to
# Display
#     All async tasks
#     Only Zombie tasks
# Delete
#     All tasks
#     All Zombie tasks
#     A task related to a given task id
#     A Zombie task related to a given task id
#     All tasks related to a given command id
#     All Zombie tasks related to a given command id
#  Flags may be added (-C, -J) to specify if Job Steps & Compensation data
#  should be cleaned as well.
###############################################################################################################

cd "$(dirname "$0")"
. ./common.sh


#setting defaults
set_defaults

usage() {
    cat << __EOF__
Usage: $0 [options]

    -h            - This help text.
    -v            - Turn on verbosity                         (WARNING: lots of output)
    -l LOGFILE    - The logfile for capturing output          (def. ${LOGFILE})
    -s SERVERNAME - The database servername for the database  (def. ${SERVERNAME})
    -p PORT       - The database port for the database        (def. ${PORT})
    -u USERNAME   - The username for the database             (def. engine)
    -d DATABASE   - The database name                         (def. ${DATABASE})
    -t TASK_ID    - Removes a task by its Task ID.
    -c COMMAND_ID - Removes all tasks related to the given Command Id.
    -z            - Removes/Displays a Zombie task.
    -R            - Removes all Zombie tasks.
    -C            - Clear related compensation entries.
    -J            - Clear related Job Steps.
    -A            - Clear all Job Steps and compensation entries.
    -q            - Quite mode, do not prompt for confirmation.

__EOF__
}

#Using two variables for sql commands in order to control command priority where data should be removed first from
#business_entity_snapshot and step table before removing it from the async_tasks table.
CMD1="";
CMD2="";
TASK_ID=""
COMMAND_ID=""
ZOMBIES_ONLY=
CLEAR_ALL=
CLEAR_COMPENSATION=
CLEAR_JOB_STEPS=
CLEAR_JOB_STEPS_AND_COMPENSATION=
QUITE_MODE=
FIELDS="task_id,task_type,status,started_at,result,action_type as command_type,command_id,step_id,storage_pool_id as DC"

while getopts hvl:s:p:u:d:t:c:zRCJAq option; do
    case "${option}" in
       \?) usage; exit 1;;
        h) usage; exit 0;;
        v) VERBOSE=1;;
        l) LOGFILE="${OPTARG}";;
        s) SERVERNAME="${OPTARG}";;
        p) PORT="${OPTARG}";;
        u) USERNAME="${OPTARG}";;
        d) DATABASE="${OPTARG}";;
        t) TASK_ID="${OPTARG}";;
        c) COMMAND_ID="${OPTARG}";;
        z) ZOMBIES_ONLY=1;;
        R) CLEAR_ALL=1;;
        C) CLEAR_COMPENSATION=1;;
        J) CLEAR_JOB_STEPS=1;;
        A) CLEAR_JOB_STEPS_AND_COMPENSATION=1;;
        q) QUITE_MODE=1;;
    esac
done

caution() {
    if [ -z "${QUITE_MODE}" ]; then
        # Highlight the expected results of selected operation.
        cat << __EOF__
$(tput smso) $1 $(tput rmso)
Caution, this operation should be used with care. Please contact support prior to running this command
Are you sure you want to proceed? [y/n]
__EOF__
        read answer
        if [ "${answer}" = "n" ]; then
           echo "Please contact support for further assistance."
           exit 1
        fi
    fi
}

if [ -z "${USERNAME}" ]; then
    echo "Please specify user name"
    exit 1
fi


# Install taskcleaner procedures
psql -w -U "${USERNAME}" -h "${SERVERNAME}" -p "${PORT}" -f ./taskcleaner_sp.sql "${DATABASE}" > /dev/null
status=$?
if [ ${status} -ne 0 ]; then
    exit ${status}
fi

if [ "${TASK_ID}" != "" -o "${COMMAND_ID}" != "" -o -n "${CLEAR_ALL}" -o -n "${CLEAR_COMPENSATION}" -o -n "${CLEAR_JOB_STEPS}" ]; then #delete operations block
    if [ -n "${TASK_ID}" ]; then
        if [ -n "${ZOMBIES_ONLY}" ]; then
            CMD2="SELECT DeleteAsyncTaskZombiesByTaskId('${TASK_ID}');"
            if [ -n "${CLEAR_JOB_STEPS}" ]; then
                CMD1="SELECT DeleteJobStepsByTaskId('${TASK_ID}');"
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    caution "This will remove the given Zombie Task, its Job Steps and related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteEntitySnapshotByZombieTaskId('${TASK_ID}');"
                else
                    caution "This will remove the given Zombie Task and its related Job Steps!!!"
                fi
            else
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    caution "This will remove the given Zombie Task and related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteEntitySnapshotByZombieTaskId('${TASK_ID}');"
                else
                    caution "This will remove the given Zombie Task!!!"
                fi
            fi
        else
            CMD2="SELECT Deleteasync_tasks('${TASK_ID}');"
            if [ -n "${CLEAR_JOB_STEPS}" ]; then
                CMD1="SELECT DeleteJobStepsByTaskId('${TASK_ID}');"
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    caution "This will remove the given Task its Job Steps and related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteEntitySnapshotByTaskId('${TASK_ID}');"
                else
                    caution "This will remove the given Task and its related Job Steps!!!"
                fi
            else
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    caution "This will remove the given Task and its related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteEntitySnapshotByTaskId('${TASK_ID}');"
                else
                    caution "This will remove the given Task!!!"
                fi
            fi
        fi
    elif [ "${COMMAND_ID}" != "" ]; then
        if [ -n "${ZOMBIES_ONLY}" ]; then
            CMD2="SELECT DeleteAsyncTaskZombiesByCommandId('${COMMAND_ID}');"
            if [ -n "${CLEAR_COMPENSATION}" ]; then
                CMD1="SELECT delete_entity_snapshot_by_command_id('${COMMAND_ID}');"
                if [ -n "${CLEAR_JOB_STEPS}" ]; then
                    caution "This will remove all Zombie Tasks of the given Command its Job Steps and its related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteJobStepsByZombieCommandId('${COMMAND_ID}');"
                else
                    caution "This will remove all Zombie Tasks of the given Command and its related Compensation data!!!"
                fi
            else
                if [ -n "${CLEAR_JOB_STEPS}" ]; then
                    caution "This will remove all Zombie Tasks of the given Command and its Job Steps!!!"
                    CMD1="${CMD1}SELECT DeleteJobStepsByZombieCommandId('${COMMAND_ID}');"
                else
                    caution "This will remove all Zombie Tasks of the given Command!!!"
                fi
            fi
        else
            CMD2="SELECT DeleteAsyncTaskByCommandId('${COMMAND_ID}');"
            if [ -n "${CLEAR_COMPENSATION}" ]; then
                CMD1="SELECT delete_entity_snapshot_by_command_id('${COMMAND_ID}');"
                if [ -n "${CLEAR_JOB_STEPS}" ]; then
                    caution "This will remove all Tasks of the given Command its Job Steps and its related Compensation data!!!"
                    CMD1="${CMD1}SELECT DeleteJobStepsByCommandId('${COMMAND_ID}');"
                else
                    caution "This will remove all Tasks of the given Command and its related Compensation data!!!"
                fi
            else
                if [ -n "${CLEAR_JOB_STEPS}" ]; then
                    caution "This will remove all Tasks of the given Command and its Job Steps!!!"
                    CMD1="${CMD1}SELECT DeleteJobStepsByCommandId('${COMMAND_ID}');"
                else
                    caution "This will remove all Tasks of the given Command!!!"
                fi
            fi
        fi
    elif [ -n "${CLEAR_ALL}" ]; then
        if [ -n "${ZOMBIES_ONLY}" ]; then
            CMD2="SELECT DeleteAsyncTasksZombies();"
            if [ -n "${CLEAR_JOB_STEPS_AND_COMPENSATION}" ]; then
                caution "This will remove all Zombie Tasks in async_tasks table, and all Job Steps and Compensation data!!!"
                CMD1="SELECT DeleteAllJobs(); SELECT DeleteAllEntitySnapshot();"
            else
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    CMD1="${CMD1}SELECT DeleteEntitySnapshotZombies();"
                    if [ -n "${CLEAR_JOB_STEPS}" ]; then
                        caution "This will remove all Zombie Tasks in async_tasks table, its related Job Steps and Compensation data!!!"
                        CMD1="${CMD1}SELECT DeleteJobStepsZombies();"
                    else
                        caution "This will remove all Zombie Tasks in async_tasks table and its related Compensation data!!!"
                    fi
                else
                    if [ -n "${CLEAR_JOB_STEPS}" ]; then
                        caution "This will remove all Zombie Tasks in async_tasks table and its related Job Steps!!!"
                        CMD1="${CMD1}SELECT DeleteJobStepsZombies();"
                    else
                        caution "This will remove all Zombie Tasks in async_tasks table!!!"
                    fi
                fi
            fi
        else
            CMD2="TRUNCATE TABLE async_tasks cascade;"
            if [ -n "${CLEAR_JOB_STEPS_AND_COMPENSATION}" ]; then
                caution "This will remove all Tasks in async_tasks table, and all Job Steps and Compensation data!!!"
                CMD1="SELECT DeleteAllJobs(); SELECT DeleteAllEntitySnapshot();"
            else
                if [ -n "${CLEAR_COMPENSATION}" ]; then
                    CMD1="TRUNCATE TABLE business_entity_snapshot cascade;"
                    if [ -n "${CLEAR_JOB_STEPS}" ]; then
                        caution "This will remove all Tasks in async_tasks table, its related Job Steps and Compensation data!!!"
                        CMD1="${CMD1}TRUNCATE TABLE step cascade;"
                    else
                        caution "This will remove all async_tasks table content and its related Compensation data!!!"
                    fi
                else
                    if [ -n "${CLEAR_JOB_STEPS}" ]; then
                        caution "This will remove all Tasks in async_tasks table and its related Job Steps!!!"
                        CMD1="${CMD1}TRUNCATE TABLE step cascade;"
                    else
                        caution "This will remove all async_tasks table content!!!"
                    fi
                fi
            fi
        fi
    else
        echo "Please specify task"
        exit 1
    fi
elif [ -n "${ZOMBIES_ONLY}" ]; then #only display operations block
    CMD1="SELECT ${FIELDS} FROM GetAsyncTasksZombies();"
else
    CMD1="SELECT ${FIELDS} FROM GetAllFromasync_tasks();"
fi

psql -w -U "${USERNAME}" -h "${SERVERNAME}" -p "${PORT}" -c "${CMD1}${CMD2}" -x "${DATABASE}" -L "${LOGFILE}"
