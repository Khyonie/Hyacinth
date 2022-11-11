package coffee.khyonieheart.origami.result;

public class Ok implements Result 
{
    private Object contained;

    public Ok(Object contained)
    {
        this.contained = contained;
    }   

    @Override
    public <T> T unwrapOk(Class<? extends T> clazz) 
    {
        return clazz.cast(this.contained);
    }

    @Override
    public <T> T unwrapErr(Class<? extends T> clazz) 
    {
        throw new UnsupportedOperationException("Attempted to unwrap an Ok into an Err type");
    }

    @Override
    public boolean isOk() 
    {
        return true;
    }

    @Override
    public boolean isErr() 
    {
        return false;
    }

    @Override
    public ResultState getState() 
    {
        return ResultState.OK;
    }
}
