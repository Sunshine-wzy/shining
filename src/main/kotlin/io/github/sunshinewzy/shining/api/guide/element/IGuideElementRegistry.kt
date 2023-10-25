package io.github.sunshinewzy.shining.api.guide.element

import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.api.namespace.NamespacedId
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

interface IGuideElementRegistry {
    
    fun initFuture(): CompletableFuture<Boolean>
    
    fun reloadFuture(): CompletableFuture<Boolean>

    fun <T: IGuideElement> register(element: T): T

    fun getState(id: NamespacedId): IGuideElementState?

    fun getElement(id: NamespacedId): IGuideElement?

    fun <T: IGuideElement> getElementByType(id: NamespacedId, type: Class<T>): T?

    fun getElementOrDefault(id: NamespacedId, default: IGuideElement): IGuideElement

    fun getElementOrDefault(default: IGuideElement): IGuideElement =
        getElementOrDefault(default.getId(), default)

    fun getElementCache(): Map<NamespacedId, IGuideElement>

    fun saveElementFuture(
        element: IGuideElement,
        isCheckExists: Boolean,
        checkId: NamespacedId,
        actionBeforeInsert: Supplier<Boolean>
    ): CompletableFuture<Boolean>

    fun saveElementFuture(
        element: IGuideElement,
        isCheckExists: Boolean,
        checkId: NamespacedId
    ): CompletableFuture<Boolean> = saveElementFuture(element, isCheckExists, checkId) { true }
    
    fun saveElementFuture(element: IGuideElement): CompletableFuture<Boolean> =
        saveElementFuture(element, false, element.getId()) { true } 

    fun removeElementFuture(element: IGuideElement): CompletableFuture<Boolean>
    
    fun <T : MutableMap<NamespacedId, IGuideElement>> getLostElementsTo(map: T): T
    
    fun getLostElements(): Map<NamespacedId, IGuideElement> =
        getLostElementsTo(HashMap())
    
}