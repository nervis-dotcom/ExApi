package ex.nervisking.command;

public class example {

    public void Commands() {
        Command.builder("test")
                .aliases("extest", "tt")
                .command((sender, arg) -> {
                    if (sender.isConsole()){
                        sender.sendMessage("asb");
                        return;
                    }

                    String name = arg.toLowerCase(0);

                    if (name.equals("reload")){
                        sender.sendMessage("configuración recargada");
                        return;
                    }

                    sender.sendMessage("&fUsa /test reload");
                }).tab((sender, arguments, completions) -> {
                    if (sender.isConsole()) {
                        return completions;
                    }

                    if (arguments.has(1)) {
                        completions.add("reload");
                    }

                    return completions;
                })
                .register();
    }
}