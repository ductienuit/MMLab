package vn.edu.uit.mmlab.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceInfor
{
    private String height;

    private String width;

    private String y;

    private String x;

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getY ()
    {
        return y;
    }

    public void setY (String y)
    {
        this.y = y;
    }

    public String getX ()
    {
        return x;
    }

    public void setX (String x)
    {
        this.x = x;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [height = "+height+", width = "+width+", y = "+y+", x = "+x+"]";
    }
}
