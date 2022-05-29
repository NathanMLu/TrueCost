package me.natelu.truecost

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import me.natelu.truecost.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun test () {
        // get statements from strings.xml
//        val test = getString(R.string.test)
        val test2 = getString(R.string.test2)

//        println(test)
        println(test2)

        Toast.makeText(context, test2, Toast.LENGTH_SHORT).show()
    }

    private fun storeQuestions() {
//        val questionsDictFinal = mutableMapOf<String, MutableList<String>>()
        val questionsDict = mutableMapOf<String, String>()
        for (i in 0 until resources.getStringArray(R.array.questions_array).size) {
            questionsDict["question${i.toString()}"] = resources.getStringArray(R.array.questions_array)[i]
        }


//        for (i in 0 until resources.getStringArray(R.array.questions_array).size) {
//            val questionsDict = mutableListOf<String>()
//            questionsDict.add(resources.getStringArray(R.array.questions_array)[i])
//            questionsDictFinal["question${i.toString()}"] = questionsDict
//        }

        //make toast and display dictionary
        Toast.makeText(context, questionsDict.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call test function
        storeQuestions()

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}