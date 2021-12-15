package io.fonimus.pokedexmvvm.designsystem

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import java.util.*

class PokemonTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        @SuppressLint("SetTextI18n")
        if (isInEditMode) {
            text = "name"
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null) {
            super.setText(
                text.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                type
            )
        }
    }
}
