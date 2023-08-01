package io.github.sunshinewzy.shining.core.guide.draft

import io.github.sunshinewzy.shining.api.guide.GuideContext
import io.github.sunshinewzy.shining.api.guide.draft.IGuideDraft
import io.github.sunshinewzy.shining.api.guide.element.IGuideElement
import io.github.sunshinewzy.shining.api.guide.element.IGuideElementContainer
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementContainerState
import io.github.sunshinewzy.shining.api.guide.state.IGuideElementState
import io.github.sunshinewzy.shining.core.guide.GuideTeam
import io.github.sunshinewzy.shining.core.guide.context.AbstractGuideContextElement

class GuideDraftContext {

    class OnlyFolders : AbstractGuideContextElement(OnlyFolders) {
        companion object : GuideContext.Key<OnlyFolders> {
            val INSTANCE: OnlyFolders = OnlyFolders()
        }
    }

    class Save(
        val state: IGuideElementState,
        val team: GuideTeam,
        val context: GuideContext
    ) : AbstractGuideContextElement(Save) {
        companion object : GuideContext.Key<Save>
    }

    class MoveFolder(
        val draft: IGuideDraft,
        val previousFolder: GuideDraftFolder
    ) : AbstractGuideContextElement(MoveFolder) {
        companion object : GuideContext.Key<MoveFolder>
    }
    
    class Load(
        val team: GuideTeam,
        val context: GuideContext,
        val element: IGuideElement?,
        val elementContainer: IGuideElementContainer?,
        val elementContainerState: IGuideElementContainerState?
    ) : AbstractGuideContextElement(Load) {
        companion object : GuideContext.Key<Load>
    }
    
}