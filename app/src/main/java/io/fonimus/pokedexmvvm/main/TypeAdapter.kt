package io.fonimus.pokedexmvvm.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.fonimus.pokedexmvvm.R
import io.fonimus.pokedexmvvm.domain.PokemonTypeEntity

class TypeAdapter(
    private val onCheckedChangeListener: (PokemonTypeEntity, Boolean) -> Unit
) :
    ListAdapter<PokemonTypeEntity, TypeViewHolder>(TypeDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TypeViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.type_itemview, parent, false)
    )


    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.bind(getItem(position), onCheckedChangeListener)
    }

}

object TypeDiffUtil : DiffUtil.ItemCallback<PokemonTypeEntity>() {
    override fun areItemsTheSame(oldItem: PokemonTypeEntity, newItem: PokemonTypeEntity) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: PokemonTypeEntity, newItem: PokemonTypeEntity) =
        oldItem == newItem
}

class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val typeChip: Chip = itemView.findViewById(R.id.type_chip)

    fun bind(
        type: PokemonTypeEntity,
        onCheckedChangeListener: (PokemonTypeEntity, Boolean) -> Unit
    ) {
        typeChip.text = type.name
        typeChip.isCheckable = true
        typeChip.setChipIconResource(type.icon)
        typeChip.setChipBackgroundColorResource(R.color.grey)
        typeChip.isCheckedIconVisible = false
        typeChip.setOnCheckedChangeListener { _, checked: Boolean ->
            onCheckedChangeListener(type, checked)
            if (checked) {
                typeChip.setChipBackgroundColorResource(type.color)
            } else {
                typeChip.setChipBackgroundColorResource(R.color.grey)
            }
            typeChip.isCloseIconVisible = checked
        }
//        addView(typeChip)
    }

}
