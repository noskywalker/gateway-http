package com.baidu.fbu.mtp.model;

public enum VerificationType {
    /** 额度申请验证: 微博验证. */
    WEIBO(1),
    /** 额度申请验证: 银联验证，获取问题. */
    YINLIAN(22),
    /** 额度申请验证: 银联验证，回答答案. */
    YINLIAN_SUBMIT(29),
    /** 额度申请验证: 邮箱. */
    BILLMAIL(30),
    /** 额度申请验证: 基本信息. */
    BASIC(5),
    /** 额度申请验证: 银联验证历史交易. */
    YINLIAN_HIS(515),
    /** 额度申请验证: 银联验证短信验证. */
    YINLIAN_MSG(259),
    /** 额度申请验证: 银联验证小额打款. */
    YINLIAN_BILL(1027),
    /** 额度申请验证: 信用卡. */
    CASHCARD(8),
    /** 额度申请验证: 设备. */
    DEVICE(10),
    /** 额度申请验证: 请求以及渠道. */
    REQUESTINFO(11),
    /** 额度申请验证: 手机验证码. */
    MOBILEPHONE(13),
    /** 额度申请验证: 交易密码 */
    TRANSACTIONPWD(14),
    /** 额度申请验证: 邀请码 */
    INVITATIONCODE(15),
    /** 额度申请验证: 通讯录 */
    CONTACTBOOK(16),
    /** 额度申请验证: 学信验证. */
    GUOXIN(41),
    /** 额度申请验证: 真人照片和身份证照片. */
    PHOTO(42),
    /** 额度申请验证: 背景，工作单位信息. */
    BACKGROUND(43),
    /** 额度申请验证: 联系人. */
    CONTACT(44),
    /** 额度申请验证: 贴吧验证. */
    TIEBA(45),
    /** QQ验证 */
    QQ(50),
    /** 浏览器验证 */
    BROWSER(46),
    /** 亲属验证 */
    FAMILY(47),
    /** 钱包商品验证 */
    QIANBAO(48),
    /** 客户类型验证 */
    CUSTOMERTYPE(51), // 710迭代添加
    /** 非本人信息验证 */
    OTHERSINFO(52), // 710迭代添加
    /**银行卡开卡信息验证 */
    CARDINFO(53), // 730迭代添加
    /**图片码验证*/
    IMGVERIFY(54), // 915迭代添加
    /** 国政信息信息验证 */
    GUOZHENGINFO(55), // 915迭代添加
    /** 去哪儿 QUNAR user_data_list */
    QUNARDATA(60), // 
    /** 去哪儿 QUNAR 最终额度 */
    QUNARCREDIT(61), 
    /** 去哪儿 consume_data_list */
    QUNARCONSUME(62);

    private int value;

    private VerificationType(final int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static VerificationType valueOf(int value) {
        switch (value) {
            case 1:
                return WEIBO;
            case 41:
                return GUOXIN;
            case 22:
                return YINLIAN;
            case 29:
                return YINLIAN_SUBMIT;
            case 30:
                return BILLMAIL;
            case 5:
                return BASIC;
            case 515:
                return YINLIAN_HIS;
            case 259:
                return YINLIAN_MSG;
            case 1027:
                return YINLIAN_BILL;
            case 43:
                return BACKGROUND;
            case 8:
                return CASHCARD;
            case 44:
                return CONTACT;
            case 10:
                return DEVICE;
            case 11:
                return REQUESTINFO;
            case 42:
                return PHOTO;
            case 13:
                return MOBILEPHONE;
            case 14:
                return TRANSACTIONPWD;
            case 15:
                return INVITATIONCODE;
            case 16:
                return CONTACTBOOK;
            case 45:
                return TIEBA;
            case 50:
                return QQ;
            case 46:
                return BROWSER;
            case 47:
                return FAMILY;
            case 48:
                return QIANBAO;
            case 51:
                return CUSTOMERTYPE;
            case 52:
                return OTHERSINFO;
            case 53:
                return CARDINFO;
            case 54:
                return IMGVERIFY;
            case 55:
                return GUOZHENGINFO;
            case 60:
                return QUNARDATA;
            case 61:
                return QUNARCREDIT;
            case 62:
                return QUNARCONSUME;
            default:
                return null;
        }
    }
}
