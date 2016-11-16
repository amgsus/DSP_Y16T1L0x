package dsplab.gui.context.impl;

import dsplab.gui.Stages;
import dsplab.gui.context.AppContext;
import dsplab.gui.stage.main.MainStage;

import static dsplab.common.Const.MAIN;

public class WTFContext implements AppContext
{
    WTFContext() {}
    public static AppContext newInstance() { return new WTFContext(); }

    @Override
    public
    void init()
    {
        mainStage = Stages.getFactory().giveMeSomethingLike(MAIN);
    }

    private MainStage mainStage;

    @Override
    public
    void show()
    {
        mainStage.show();
    }
}
