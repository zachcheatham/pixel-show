package me.zachcheatham.pixelshow.show.effect;

public class EffectProperty<T>
{
    T value = null;
    private final String name;

    public EffectProperty(String nameTraslation, T value)
    {
        this.name = nameTraslation;
        this.value = value;
    }

    public String getNameTranslation()
    {
        return name;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public Class<?> getType()
    {
        return value.getClass();
    }
}
