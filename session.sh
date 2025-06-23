# linked as xmlbuilders.session.sh in ~/.tmuxifier/layouts
session_root "~/dev/groovy/gradle-libreoffice-xmlbuilders"

if initialize_session "xmlbuilders"; then

  new_window "neovim"
  split_v 25
  run_cmd "nodemon --config watch.json"
  select_pane 0
  run_cmd "nvim"
  cp "$session_root/src/test/resources/favicon.ico" "$session_root/build/reports"
  new_window "server"
  run_cmd "serve -L build/reports/tests"
  select_window 0

fi

finalize_and_go_to_session
