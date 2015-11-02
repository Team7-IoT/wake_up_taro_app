package com.team7.wakeuptaroapp.util;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.SharedPreferenceMode;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * {@link android.content.SharedPreferences}�̑�����ȗ������郉�b�p�[�N���X�B
 *
 * @author Naotake.K
 * @see <a href="http://dkunzler.github.io/esperandro/">Esperandro</a>
 */
@SharedPreferences(name = "wake_up_taro", mode = SharedPreferenceMode.PRIVATE)
public interface TaroSharedPreference extends SharedPreferenceActions {

    /**
     * �e�@�ƂȂ�f�o�C�X�����擾����B<br />
     * �e�@�����ݒ�̏ꍇ�A�󕶎���Ԃ��B
     *
     * @return �f�o�C�X��
     */
    @Default(ofString = "")
    String deviceName();

    /**
     * �e�@�ƂȂ�f�o�C�X����ۑ�����B
     *
     * @param deviceName �f�o�C�X��
     */
    void deviceName(String deviceName);
}
