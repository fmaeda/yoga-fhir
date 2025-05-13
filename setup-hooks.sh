#!/usr/bin/env bash

# regex to validate in commit msg
# https://regex101.com/r/VTEL5G/3
commit_regex='(Merge(.*)|([A-Z_]+-[0-9]+)|((\d+\.)?(\d+\.)?(\d+).(RELEASE|SNAPSHOT)))'
error_msg="Mensagem de commit deve iniciar com o número da issue JIRA (ex. 'MME-xxx', 'INC-xxx'), 'Merge(d)'"
echo "#!/usr/bin/env bash

# Part 1
stagedFiles=$(git diff --staged --name-only)
# Part 2
echo \"Running spotlessApply. Formatting code...\"
./mvnw spotless:apply
# Part 3
for file in $stagedFiles; do
  if test -f \"$file\"; then
    git add $file
  fi
done

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
echo "Setup do Script do pre-commit hook finalizado!"