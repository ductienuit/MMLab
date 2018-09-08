package vn.edu.uit.mmlab.modal;

public class UploadSuccess
{
    private String fileID;

    private String fileName;

    private String msg;

    public String getFileID ()
    {
        return fileID;
    }

    public void setFileID (String fileID)
    {
        this.fileID = fileID;
    }

    public String getFileName ()
    {
        return fileName;
    }

    public void setFileName (String fileName)
    {
        this.fileName = fileName;
    }

    public String getMsg ()
    {
        return msg;
    }

    public void setMsg (String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [fileID = "+fileID+", fileName = "+fileName+", msg = "+msg+"]";
    }
}
