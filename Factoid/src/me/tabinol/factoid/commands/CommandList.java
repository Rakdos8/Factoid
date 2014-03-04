package me.tabinol.factoid.commands;

public enum CommandList {

    RELOAD,
    SELECT,
    EXPAND,
    CREATE,
    AREA,
    OWNER,
    FLAG,
    PERMISSION,
    RESIDENT,
    BAN,
    REMOVE,
    CONFIRM,
    CANCEL,
    HERE,
    ADMINMOD,
    PAGE,
    DEFAULT,
    PRIORITY,
    APPROVE,
    RENAME,
    HELP;

    // If a command has a second name
    public enum SecondName {

        CURRENT(HERE);

        public final CommandList mainCommand;

        SecondName(CommandList mainCommand) {

            this.mainCommand = mainCommand;
        }
    }

}
