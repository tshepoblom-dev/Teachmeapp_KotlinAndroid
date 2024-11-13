package com.lumko.teachme.model.view

import com.lumko.teachme.model.ChapterItem

class ChapterListItemFactory {
    companion object {
        fun getItem(chapterItem: ChapterItem): CourseCommonItem {
            when (chapterItem.type) {
                ChapterItem.Type.SESSION.value -> return SessionChapterListItem(chapterItem)
                ChapterItem.Type.FILE.value -> return FileChapterListItem(chapterItem)
                ChapterItem.Type.TEXT.value -> return TextChapterListItem(chapterItem)
                ChapterItem.Type.ASSIGNEMENT.value -> return AssignmentChapterListItem(chapterItem)
                ChapterItem.Type.QUIZ.value -> return QuizChapterListItem(chapterItem)
                ChapterItem.Type.CERTIFICATE.value -> return CertChapterListItem(chapterItem)
            }
            return SessionChapterListItem(chapterItem)
        }
    }
}