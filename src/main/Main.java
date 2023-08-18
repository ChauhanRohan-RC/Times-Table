package main;

import main.ui.GlConfig;
import main.ui.TimesTableUi;
import main.util.Ui;


public class Main {

    public static void mainLaunch(String[] args) {
        Ui.considerPost(() -> {
            final TimesTableUi ui = new TimesTableUi();
//            ui.setPlay(true);
        });
    }

    public static void mainTest(String[] args) {

    }

    public static void main(String[] args) {
        R.init();
        GlConfig.init();

//        mainTest(args);
        mainLaunch(args);
    }

}
