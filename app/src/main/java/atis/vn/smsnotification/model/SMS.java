package atis.vn.smsnotification.model;

public class SMS {
    private int id;
    private String type;
    private String content;
    private String code;
    private String sender;
    private String received_time;
    private String send_time;

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    private int attempt;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceivedTime() {
        return received_time;
    }

    public void setReceivedTime(String received_time) {
        this.received_time = received_time;
    }

    public String getSendTime() {
        return send_time;
    }

    public void setSendTime(String send_time) {
        this.send_time = send_time;
    }

}
