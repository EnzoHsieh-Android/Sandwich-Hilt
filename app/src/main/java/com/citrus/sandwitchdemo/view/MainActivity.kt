package com.citrus.sandwitchdemo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.citrus.sandwitchdemo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


/**Hilt setup 06 - 應用inject的Activity和子節點Fragment都須加上@AndroidEntryPoint*/
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}