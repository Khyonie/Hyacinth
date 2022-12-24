package coffee.khyonieheart.origami.testing;

import coffee.khyonieheart.origami.module.OrigamiModule;

public class TestModule implements OrigamiModule
{
    private static OrigamiModule INSTANCE;

    @Override
    public void onEnable() 
    {
        INSTANCE = this;
    }

    @Override
    public void onDisable() 
    {
        
    }

    public static OrigamiModule getInstance()
    {
        return INSTANCE;
    }
}
