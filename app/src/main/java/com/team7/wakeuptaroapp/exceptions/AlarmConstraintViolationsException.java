package com.team7.wakeuptaroapp.exceptions;

/**
 * アラーム登録時のバリデーションにて違反があったことを表すチェック例外クラス。
 *
 * @author Naotake.K
 */
public class AlarmConstraintViolationsException extends Exception {

    private final int causeMessageId;

    /**
     * 違反内容を表すメッセージ ID を指定して例外情報を生成する。
     *
     * @param causeMessageId 違反内容を表すメッセージ ID
     */
    public AlarmConstraintViolationsException(int causeMessageId) {
        super();
        this.causeMessageId = causeMessageId;
    }

    public int getCauseMessageId() {
        return causeMessageId;
    }
}
