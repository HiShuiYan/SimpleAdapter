package com.common.commonadapter.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.common.commonadapter.listener.HolderCallBack
import java.lang.reflect.ParameterizedType

//外部holder继承使用
abstract class ItemViewHolder<T : Any> : RecyclerView.ViewHolder {

    constructor(
            context: Context,
            parent: ViewGroup?,
            @LayoutRes layoutId: Int
    ) : super(LayoutInflater.from(context).inflate(layoutId, parent, false)) {
        this.context = context
    }

    constructor(itemView: View) : super(itemView) {
        this.context = itemView.context
    }

    var context: Context
    private val views: SparseArray<View> = SparseArray()
    var holderCallback: HolderCallBack? = null

    fun <T : View> getView(@IdRes viewId: Int): T? {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            if (view == null) return null
            views.put(viewId, view)
        }
        return view as T
    }

    //删掉缓存从新获取View
    fun removeView(@IdRes viewId: Int) {
        views.remove(viewId)
    }

    //View
    fun setGone(@IdRes viewId: Int, gone: Boolean): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.setVisibility(if (gone) View.GONE else View.VISIBLE)
        return this
    }

    fun setVisible(@IdRes viewId: Int, visible: Boolean): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.setVisibility(if (visible) View.VISIBLE else View.INVISIBLE)
        return this
    }

    fun setVisibleOrGone(@IdRes viewId: Int, visible: Boolean): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.setVisibility(if (visible) View.VISIBLE else View.GONE)
        return this
    }

    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.setBackgroundResource(backgroundRes)
        return this
    }

    //TextView
    fun tv(@IdRes viewId: Int): TextView? {
        return getView<TextView>(viewId)
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): ItemViewHolder<T> {
        val view: TextView? = getView<TextView>(viewId)
        view?.setText(value)
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): ItemViewHolder<T> {
        val view = getView<TextView>(viewId)
        view?.setText(strId)
        return this
    }

    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): ItemViewHolder<T> {
        val view = getView<TextView>(viewId)
        view?.setTextColor(textColor)
        return this
    }

    //ImageView
    fun iv(@IdRes viewId: Int): ImageView? {
        return getView<ImageView>(viewId)
    }

    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable): ItemViewHolder<T> {
        val view = getView<ImageView>(viewId)
        view?.setImageDrawable(drawable)
        return this
    }

    fun setImageRes(@IdRes viewId: Int, @DrawableRes imgRes: Int): ItemViewHolder<T> {
        val view = getView<ImageView>(viewId)
        view?.setImageResource(imgRes)
        return this
    }

    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap): ItemViewHolder<T> {
        val view = getView<ImageView>(viewId)
        view?.setImageBitmap(bitmap)
        return this
    }

    fun setSelect(@IdRes viewId: Int, selected: Boolean): ItemViewHolder<T> {
        val view = getView<View>(viewId)
        view?.isSelected = selected
        return this
    }

    //setOnClick
    fun setOnClick(@IdRes viewId: Int) {
        val view = getView<View>(viewId)
        if (view != null) {
            view.setOnClickListener({
                onViewClick(view.id)
            })
        }
    }

    //获取holder的实际泛型
    fun getRealType(): Class<*>? {
        // 获取当前new的对象的泛型的父类类型
        val pt = this.javaClass.genericSuperclass
        if (pt is ParameterizedType) {
            // 获取第一个类型参数的真实类型
            return pt.getActualTypeArguments()[0] as Class<T>
        }
        return null
    }

    //用于和外部交互回调
    fun setCallBack(callback: HolderCallBack) {
        this.holderCallback = callback
    }

    open fun bindFragment(fragment: Fragment?){}

    open fun onPageResume() {}

    open fun onPagePause() {}

    open fun attachWindow() {}

    open fun detachWindow() {}

    open fun onViewClick(@IdRes viewId: Int) {}

    open fun getSpanCount(): Int { return 1 }

    abstract fun onBindViewHolder(data: T, postion: Int)
}
