package me.zachcheatham.pixelshow.show.effect;

public class EffectProperty<T>
{
    T value = null;
    private final String name;

    public EffectProperty(String nameTranslation, T value)
    {
        this.name = nameTranslation;
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
