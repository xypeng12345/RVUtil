package cn.xuyuan.utillib

import android.content.Context
import android.widget.Toast

object ToastUtil {
    private var mToast: Toast? = null

    /**
     * 显示short message
     * @param context 全局context
     * @param resId string string资源id
     */
    fun showToast(context: Context, resId: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(resId)
            mToast!!.duration = Toast.LENGTH_SHORT
        }
        mToast!!.show()
    }

    /**
     * 显示short message
     * @param context 全局context
     * @param message 显示msg
     */
    fun showToast(context: Context, message: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(message)
            mToast!!.duration = Toast.LENGTH_SHORT
        }
        mToast!!.show()
    }
}