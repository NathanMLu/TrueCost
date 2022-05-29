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

    private val questionsDictFinal = mutableMapOf<String, MutableList<String>>()

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

    private fun setQuestions(name: String, questions: Array<String>){

        for (i in questions.indices - 1) {
            questionsDictFinal[name] = questions.toMutableList()
        }

        Toast.makeText(context, questionsDictFinal.toString(), Toast.LENGTH_SHORT).show()

    }

    private fun getQuestions(key: String): MutableList<String> {
        val questions = mutableListOf<String>()

        for (i in questionsDictFinal.keys) {
            if (i == key) {
                questions.addAll(questionsDictFinal[i]!!)
            }
        }

        if (questions.isEmpty()) {
            Toast.makeText(context, "No questions found", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, questions.toString(), Toast.LENGTH_SHORT).show()
        }

        return questions

        //make toast and display dictionary
//        Toast.makeText(context, questionsDict.toString(), Toast.LENGTH_SHORT).show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}