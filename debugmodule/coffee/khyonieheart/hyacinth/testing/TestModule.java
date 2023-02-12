package coffee.khyonieheart.hyacinth.testing;

import coffee.khyonieheart.hyacinth.module.HyacinthModule;

public class TestModule implements HyacinthModule
{
    private static HyacinthModule INSTANCE;

    @Override
    public void onEnable() 
    {
        INSTANCE = this;
    }

    @Override
    public void onDisable() 
    {
        
    }

    public static HyacinthModule getInstance()
    {
        return INSTANCE;
    }
}
