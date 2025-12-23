package com.example.myapplication

import androidx.activity.EdgeToEdge

class errorsimulado : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        EdgeToEdge.enable(this)
        setContentView(R.layout.activity_errorsimulado)
        TODO(
            """
            |Cannot convert element
            |With text:
            |ViewCompat.setOnApplyWindowInsetsListener(<android.view.View>findViewById(R.id.main), (v, insets) -> {
            |            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            |            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            |            return insets;
            |        }
            """.trimMargin()
        )
    }
}