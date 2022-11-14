package com.nikhil.flowpoc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nikhil.flowpoc.databinding.FragmentFirstBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private lateinit var fixedFlow: Flow<Int>;
    private lateinit var collectionFlow: Flow<Int>;
    private lateinit var lamdaFlow: Flow<Int>;
    private lateinit var channelFlow: Flow<Int>;
    private val list = listOf<Int>(1,2,3,4,5);

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object{
        private const val TAG = "FirstFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        //exampleFixedFlow()
       // exampleCollectionFlow()
       //exampleLamdaFlow()
       // exampleChannelFlow()
        //exampleFlowChain()
       // exampleMapFlow()
      //  exampleFilterFlow()
      //  exampleTakeFlow()
      // exampleTakeWhileFlow()
       // exampleZip()
        exampleCombine()
    }

    private fun exampleCombine() {

        val oddNumbers =   (1..20).asFlow()
            .filter { oddNumbers(it) }
        val evenNumbers =     (1..40).asFlow()
            .filter { evenNumbers(it) }
        runBlocking {
            oddNumbers.combine(evenNumbers){
                    a,b -> "Numbers are $a,$b"
            }.collect{
                Log.d(TAG, "exampleCombine: $it")
            }
        }
    }

    private fun exampleZip() {   
    val oddNumbers =   (1..20).asFlow()
        .filter { oddNumbers(it) }
     val evenNumbers =     (1..40).asFlow()
         .filter { evenNumbers(it) }
        runBlocking {
            oddNumbers.zip(evenNumbers){
                a,b -> "Numbers are $a,$b"
            }.collect{
                Log.d(TAG, "exampleZip: $it")
            }
        }

    }

    private fun exampleTakeWhileFlow() {
        val startTime = System.currentTimeMillis()
        runBlocking {
            (1 .. 1000).asFlow()
                .takeWhile { System.currentTimeMillis() - startTime <= 100L  }
                .collect{
                    Log.d(TAG, "exampleTakeWhileFlow: $it")
                }
        }

    }

    private fun exampleTakeFlow() {
        runBlocking {
            (1..20).asFlow()
                .take(5)
                .collect {
                    Log.d(TAG, "exampleTakeFlow: $it")
                }
        }
    }

    private fun exampleFilterFlow() {
        runBlocking {
            (1..20).asFlow()
                .filter { oddNumbers(it) }
                .collect {
                    Log.d(TAG, "exampleFilterFlow: $it")
                }
        }
    }

    private suspend fun oddNumbers(num: Int):Boolean {
        delay(1000)
        return num % 2 == 1

    }

    private suspend fun evenNumbers(num: Int):Boolean {
        delay(500)
        return num % 2 == 0

    }

    private fun exampleMapFlow() {
        runBlocking {
            (1..10).asFlow()
                .map { performSyncOperation(it) }
                .collect {
                    Log.d(TAG, "exampleMapFlow: $it")
                }
        }
    }

    private suspend fun performSyncOperation(it: Int):String {
        delay(1000)
        return "map is $it"

    }

    private fun exampleFlowChain() {
        runBlocking{
            createFlowChain()
        }

    }

    private suspend fun createFlowChain() {

       /*
        withContext(Dispatchers.IO) is not allowed in Flow should be run in same context as collect
        we can sepcfy dispatcher using flow on
       flow{
            withContext(Dispatchers.IO){
                (10..20).forEach{
                    Log.d(TAG, "createFlowChain: ${Thread.currentThread().name}")
                    delay(1000)
                    emit(it)
                }
            }
        }.collect{
            Log.d(TAG, "collect: ${Thread.currentThread().name}")
            Log.d(TAG, "createFlowChain: $it")
        }*/

        flow{
            (10..20).forEach{
                Log.d(TAG, "createFlowChain: ${Thread.currentThread().name}")
                delay(1000)
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
            .collect{
            Log.d(TAG, "collect: ${Thread.currentThread().name}")
            Log.d(TAG, "createFlowChain: $it")
        }
    }

    private fun exampleChannelFlow() {
        setupChannnelFlow()
        collectChannelFlow()
    }

    private fun collectChannelFlow() {
        lifecycleScope.launch(Dispatchers.Main){
            channelFlow.collect{
                Log.d(TAG, "ChannelFlow: $it")
            }
        }
    }

    private fun setupChannnelFlow() {
        channelFlow = channelFlow {
            (1 .. 5).forEach {
                delay(1000)
                send(it)
            }
        }
    }

    private fun exampleLamdaFlow() {
        setupLamdaFlow()
       collectLamdaFlow()

    }

    private fun collectLamdaFlow() {
        lifecycleScope.launch(Dispatchers.Main){
            lamdaFlow.collect{
                Log.d(TAG, "LamdaFlow: $it")
            }
        }

    }

    private fun setupLamdaFlow() {
        lamdaFlow = flow{
            (1..5).forEach{
                delay(1000)
                emit(it)
            }
        }

    }

    private fun exampleCollectionFlow() {
        setupCollectionFlow()
        collectCollectionFlow()

    }

    private fun collectCollectionFlow() {
        lifecycleScope.launch(Dispatchers.Main){
            collectionFlow.collect{
                Log.d(TAG, "CollectionFlow: $it")
            }
        }
    }

    private fun setupCollectionFlow() {
        collectionFlow = list.asFlow().onEach {
            delay(1000)
        }
    }

    private fun exampleFixedFlow() {
        setupFixedFlow()
        collectFixedFlow()
    }

    private fun collectFixedFlow() {
        lifecycleScope.launch(Dispatchers.Main){
            fixedFlow.collect{
                Log.d(TAG, "FixedFlow: $it")
            }
        }

    }

    private fun setupFixedFlow() {
        fixedFlow   = flowOf(1,2,3,4,5,6).onEach {
            delay(1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}