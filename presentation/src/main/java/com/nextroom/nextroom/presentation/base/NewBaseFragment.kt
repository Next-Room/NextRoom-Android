package com.nextroom.nextroom.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.extension.updateSystemPadding

// 기존에 사용하던 BaseFragment를 다 걷어내면
// 이 파일을 BaseFragment로 이름을 변경해 사용한다.
abstract class NewBaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var _binding: VB? = null
    val binding: VB
        get() = checkNotNull(_binding) { "binding is null" }

    abstract val screenName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSystemPadding()

        initListeners()
        initObserve()
        setFragmentResultListeners()
        initViews()
    }

    open fun initListeners() {}
    open fun initObserve() {}
    open fun setFragmentResultListeners() {}
    open fun initViews() {}

    override fun onResume() {
        super.onResume()

        // TODO: 스크린 이벤트 남기기
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}