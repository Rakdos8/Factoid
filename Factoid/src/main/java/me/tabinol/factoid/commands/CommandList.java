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
    INFO,
    ADMINMOD,
    PAGE,
    DEFAULT,
    PRIORITY,
    APPROVE,
    RENAME,
    HELP,
    KICK,
    WHO,
    NOTIFY;

    // If a command has a second name
    public enum SecondName {

        CURRENT(INFO),
        HERE(INFO);

        public final CommandList mainCommand;

        SecondName(CommandList mainCommand) {

            this.mainCommand = mainCommand;
        }
    }

}
