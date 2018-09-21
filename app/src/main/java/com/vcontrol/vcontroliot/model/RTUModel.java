package com.vcontrol.vcontroliot.model;

import java.io.Serializable;

/**
 {
 "status": 1,
 "msg": "ok",
 "data": {

 }
 }
 */
public class RTUModel implements Serializable
{
    private static final long serialVersionUID = -828322761336296999L;

  /* {
    "status": 1,
    "msg": "ok",
    "data": {
        "id": 3057,
        "stationRtuno": "82150015006896",
        "stationAddrno": "/117.136.38.151:28728",
        "orgRecvrecordid": null,
        "recvTime": 1487833836000,
        "sendTime": 1487833789000,
        "collectingTime": 1487833789000,
        "messageName": "查询遥测终端软件版本",
        "messageType": 69,
        "elementName": "软件版本",
        "elementType": -1,
        "elementContent": "V2.2392016110701",
        "messageRecvstate": "数据正常",
        "messageQitastate": "存储成功"
    }
}*/

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getStationRtuno()
    {
        return stationRtuno;
    }

    public void setStationRtuno(String stationRtuno)
    {
        this.stationRtuno = stationRtuno;
    }

    public String getStationAddrno()
    {
        return stationAddrno;
    }

    public void setStationAddrno(String stationAddrno)
    {
        this.stationAddrno = stationAddrno;
    }

    public String getOrgRecvrecordid()
    {
        return orgRecvrecordid;
    }

    public void setOrgRecvrecordid(String orgRecvrecordid)
    {
        this.orgRecvrecordid = orgRecvrecordid;
    }

    public long getRecvTime()
    {
        return recvTime;
    }

    public void setRecvTime(long recvTime)
    {
        this.recvTime = recvTime;
    }

    public long getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(long sendTime)
    {
        this.sendTime = sendTime;
    }

    public long getCollectingTime()
    {
        return collectingTime;
    }

    public void setCollectingTime(long collectingTime)
    {
        this.collectingTime = collectingTime;
    }

    public String getMessageName()
    {
        return messageName;
    }

    public void setMessageName(String messageName)
    {
        this.messageName = messageName;
    }

    public int getMessageType()
    {
        return messageType;
    }

    public void setMessageType(int messageType)
    {
        this.messageType = messageType;
    }

    public String getElementName()
    {
        return elementName;
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }

    public String getElementType()
    {
        return elementType;
    }

    public void setElementType(String elementType)
    {
        this.elementType = elementType;
    }

    public String getElementContent()
    {
        return elementContent;
    }

    public void setElementContent(String elementContent)
    {
        this.elementContent = elementContent;
    }

    public String getMessageRecvstate()
    {
        return messageRecvstate;
    }

    public void setMessageRecvstate(String messageRecvstate)
    {
        this.messageRecvstate = messageRecvstate;
    }

    public String getMessageQitastate()
    {
        return messageQitastate;
    }

    public void setMessageQitastate(String messageQitastate)
    {
        this.messageQitastate = messageQitastate;
    }

    public int id;
    public String stationRtuno;
    public String stationAddrno;
    public String orgRecvrecordid;
    public long recvTime;
    public long sendTime;
    public long collectingTime;
    public String messageName;
    public int messageType;
    public String elementName;
    public String elementType;
    public String elementContent;
    public String messageRecvstate;
    public String messageQitastate;



}
