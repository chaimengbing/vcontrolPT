package com.vcontrol.vcontroliot.model;

public class MainInfo
{
	
	private int id;
	private int drawable;
	private String name;

	
	
	public MainInfo(int id, int drawable, String name)
	{
		super();
		this.id = id;
		this.drawable = drawable;
		this.name = name;
	}

	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getDrawable()
	{
		return drawable;
	}
	public void setDrawable(int drawable)
	{
		this.drawable = drawable;
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
