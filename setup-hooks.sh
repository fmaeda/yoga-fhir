#!/usr/bin/env bash

# regex to validate in commit msg
# https://regex101.com/r/VTEL5G/3
commit_regex='(Merge(.*)|([A-Z_]+-[0-9]+)|((\d+\.)?(\d+\.)?(\d+).(RELEASE|SNAPSHOT)))'
error_msg="Mensagem de commit deve iniciar com o número da issue JIRA (ex. 'MME-xxx', 'INC-xxx'), 'Merge(d)'"
echo "#!/usr/bin/env bash
./mvnw spotless:apply
commit_regex='$commit_regex'
error_msg=\"$error_msg\"
if ! grep -qE \"\$commit_regex\" \"\$1\"; then
    echo \"\$error_msg\" >&2
    exit 1
fi
" > .git/hooks/commit-msg
chmod +x .git/hooks/commit-msg
chmod +x mvnw
#rm .git/hooks/commit-msg.sample
echo "Completed git hook commit message setup"