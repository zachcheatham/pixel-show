package me.zachcheatham.pixelshow.show;

public class Layer
{
    private String name;

    public Layer(String layerName)
    {
        name = layerName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
