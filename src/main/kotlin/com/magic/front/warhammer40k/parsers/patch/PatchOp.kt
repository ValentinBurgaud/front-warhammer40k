package com.magic.front.warhammer40k.parsers.patch

enum class PatchOp(val code: String) {

    REPLACE("replace"),
    REMOVE("remove"),
    ADD("add");

    companion object {

        fun byCode(code: String): PatchOp {
            return when (code) {
                "replace" -> REPLACE
                "remove" -> REMOVE
                "add" -> ADD
                else -> throw IllegalStateException("bad code $code")
            }
        }
    }
}