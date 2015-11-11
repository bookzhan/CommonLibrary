package cn.bookzhan.model;


/**
 *
 * 用户信息
 */
public class UserInfo {
    private String member_id;
    private String nick_name;
    private String mobile;
    private String gender;
    private String birthday;
    private String avatar;
    private String email;
    private String level;
    private String accesstoken;
    private Address default_address;
    private String pw_id;

    public Address getDefault_address() {
        return default_address;
    }

    public void setDefault_address(Address default_address) {
        this.default_address = default_address;
    }

    public String getCard_id() {
        return pw_id;
    }

    public void setCard_id(String card_id) {
        this.pw_id = card_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        if(gender==null||"".equals(gender)){
            gender="2";
        }
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
