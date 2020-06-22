package fi.ketjusovellus.ketjupilotti.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import fi.ketjusovellus.ketjupilotti.R

class LandingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_onboarding_landing, container, false)

        root.findViewById<AppCompatButton>(R.id.button_next)?.setOnClickListener {
            root.findNavController().navigate(R.id.action_landingFragment_to_encountersFragment)
        }
        return root
    }

}
