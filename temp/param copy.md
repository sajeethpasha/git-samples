flowchart TD
    Start([Start]) --> EnvSetup[Set Environment Variables]
    EnvSetup --> BuildClasspath[Build LOCAL_CLASSPATH]
    BuildClasspath --> InitLog[Initialize Logging]
    InitLog --> CovarPrompt{"Run Covariance\nSlice?"}

    CovarPrompt -->|Exit| End([End])
    
    %% Regular Flow (N)
    CovarPrompt -->|"N"| RegularFlow["Regular Flow"]
    RegularFlow --> MarginType1["Prompt: Margin Type"]
    MarginType1 --> CheckIDAY_OCC1{"Is IDAY_OCC?"}
    
    CheckIDAY_OCC1 -->|Yes| ProjectedMarginId1["Prompt: Projected Margin ID"]
    ProjectedMarginId1 --> MarginID1["Prompt: Margin ID"]
    CheckIDAY_OCC1 -->|No| MarginID1
    
    MarginID1 --> BusinessDate1["Prompt: Business Date"]
    BusinessDate1 --> VarExec1["Prompt: Execute VaR?"]
    VarExec1 --> LiqAdj1["Prompt: Execute Liquidity Adjustment?"]
    LiqAdj1 --> PostProd1{"Post-Production\nCalculation?"}
    
    PostProd1 -->|Yes| AggPosFile1["Prompt: Aggregated Position File"]
    PostProd1 -->|No| GemFlow1
    AggPosFile1 --> EDAPrompt1["Prompt: Generate EDA Events?"]
    EDAPrompt1 --> ExecuteJava1[/"Execute Java Command"\]
    
    GemFlow1{"Use GemFlow?"} -->|Yes| SecIvar1{"Load Security\nIVAR File?"}
    GemFlow1 -->|No| ResDB1["Use Resiliency DB"]
    ResDB1 --> EDAPrompt2["Prompt: Generate EDA Events?"]
    EDAPrompt2 --> ExecuteJava2[/"Execute Java Command"\]
    
    SecIvar1 -->|Yes| SecIvarFile1["Prompt: Security IVAR Filename"]
    SecIvar1 -->|No| EDAPrompt3["Prompt: Generate EDA Events?"]
    SecIvarFile1 --> EDAPrompt4["Prompt: Generate EDA Events?"]
    EDAPrompt3 --> ExecuteJava3[/"Execute Java Command"\]
    EDAPrompt4 --> ExecuteJava4[/"Execute Java Command"\]
    
    %% Covariance Flow (Y)
    CovarPrompt -->|"Y"| CovarFlow["Covariance Flow"]
    CovarFlow --> MarginType2["Prompt: Margin Type"]
    MarginType2 --> CheckIDAY_OCC2{"Is IDAY_OCC?"}
    
    CheckIDAY_OCC2 -->|Yes| ProjectedMarginId2["Prompt: Projected Margin ID"]
    ProjectedMarginId2 --> MarginID2["Prompt: Margin ID"]
    CheckIDAY_OCC2 -->|No| MarginID2
    
    MarginID2 --> BusinessDate2["Prompt: Business Date"]
    BusinessDate2 --> CovarFile["Prompt: Covariance Filename"]
    CovarFile --> VarExec2["Prompt: Execute VaR?"]
    VarExec2 --> LiqAdj2["Prompt: Execute Liquidity Adjustment?"]
    LiqAdj2 --> PostProd2{"Post-Production\nCalculation?"}
    
    PostProd2 -->|Yes| AggPosFile2["Prompt: Aggregated Position File"]
    PostProd2 -->|No| GemFlow2
    AggPosFile2 --> EDAPrompt5["Prompt: Generate EDA Events?"]
    EDAPrompt5 --> ExecuteJava5[/"Execute Java Command"\]
    
    GemFlow2{"Use GemFlow?"} -->|Yes| SecIvar2{"Load Security\nIVAR File?"}
    GemFlow2 -->|No| ResDB2["Use Resiliency DB"]
    ResDB2 --> EDAPrompt6["Prompt: Generate EDA Events?"]
    EDAPrompt6 --> ExecuteJava6[/"Execute Java Command"\]
    
    SecIvar2 -->|Yes| SecIvarFile2["Prompt: Security IVAR Filename"]
    SecIvar2 -->|No| EDAPrompt7["Prompt: Generate EDA Events?"]
    SecIvarFile2 --> EDAPrompt8["Prompt: Generate EDA Events?"]
    EDAPrompt7 --> ExecuteJava7[/"Execute Java Command"\]
    EDAPrompt8 --> ExecuteJava8[/"Execute Java Command"\]
    
    ExecuteJava1 & ExecuteJava2 & ExecuteJava3 & ExecuteJava4 & ExecuteJava5 & ExecuteJava6 & ExecuteJava7 & ExecuteJava8 --> End
    
    classDef default fill:#fff,stroke:#000,stroke-width:2px;
    classDef process fill:#fff,stroke:#000,stroke-width:2px;
    classDef decision fill:#fff,stroke:#000,stroke-width:2px;
    classDef io fill:#fff,stroke:#000,stroke-width:2px;
    
    class Start,End default;
    class EnvSetup,BuildClasspath,InitLog,RegularFlow,CovarFlow process;
    class CovarPrompt,CheckIDAY_OCC1,CheckIDAY_OCC2,PostProd1,PostProd2,GemFlow1,GemFlow2,SecIvar1,SecIvar2 decision;
    class MarginType1,MarginType2,MarginID1,MarginID2,BusinessDate1,BusinessDate2,VarExec1,VarExec2,LiqAdj1,LiqAdj2,ProjectedMarginId1,ProjectedMarginId2,AggPosFile1,AggPosFile2,SecIvarFile1,SecIvarFile2,CovarFile io;