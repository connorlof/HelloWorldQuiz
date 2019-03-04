package com.loftydevelopment.helloworldquiz

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*


class QuizActivity : AppCompatActivity() {

    var answers:ArrayList<String> = ArrayList()
    var locationOfCorrectAnswer:Int? = null
    var score = 0
    var numberOfQuestions = 0
    var languageList:ArrayList<Language> = ArrayList()
    var lastQuestion:String = ""

    var sharedPref:SharedPreferences? = null
    var checked: Boolean? = null
    var panZoomView: PanZoomView? = null

    var mpCorrect:MediaPlayer? = null
    var mpWrong:MediaPlayer? = null

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        mpCorrect = MediaPlayer.create(this, R.raw.sound_correct)
        mpWrong = MediaPlayer.create(this, R.raw.sound_wrong)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        checked = sharedPref!!.getBoolean("checked", false)


        play(findViewById(R.id.btAnswer0))

    }




    fun chooseAnswer(view: View){

        if(view.getTag().toString().equals(locationOfCorrectAnswer.toString())){
            score++
            if(checked!!) {
                mpCorrect!!.start()
            }
            tvResult.text = "Correct!"
        }else{
            if(checked!!) {
                mpWrong!!.start()
            }
            tvResult.text = "Wrong!"
        }

        numberOfQuestions++

        tvPoints.text = score.toString() + "/" + numberOfQuestions.toString()

        generateQuestion()

    }

    private fun generateQuestion(){

        var rand: Random = Random()

        var questionIndex = rand.nextInt(languageList.size)

        //Check that question is not repeating twice in a row
        while(languageList[questionIndex].name.equals(lastQuestion)){
            questionIndex = rand.nextInt(languageList.size)
        }

        lastQuestion = languageList[questionIndex].name

        val img:Int = this.getResources().getIdentifier(languageList[questionIndex].imgFileName, "drawable", packageName)
        val uri = Uri.parse("android.resource://$packageName/drawable/$img")
        ivLanguageImg.loadImageOnCanvas(uri)

        locationOfCorrectAnswer = rand.nextInt(4)

        var incorrectAnswer = ""

        answers.clear()

        for(index in 0..4){

            if(index == locationOfCorrectAnswer){
                answers.add(languageList[questionIndex].name)
            }else{
                incorrectAnswer = languageList[rand.nextInt(languageList.size)].name

                while(incorrectAnswer.equals(languageList[questionIndex].name)){
                    incorrectAnswer = languageList[rand.nextInt(languageList.size)].name
                }

                answers.add(incorrectAnswer)
            }

        }

        btAnswer0.text = answers[0]
        btAnswer1.text = answers[1]
        btAnswer2.text = answers[2]
        btAnswer3.text = answers[3]

    }

    private fun play(view: View){

        languageList = initLanguageObjs()

        score = 0
        numberOfQuestions = 0

        tvTimer.text = "60s"
        tvPoints.text = "0/0"
        tvResult.text = ""

        generateQuestion()

        timer = object: CountDownTimer(60100, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = (millisUntilFinished / 1000).toString() + "s"
            }

            override fun onFinish() {

                var weeklyRank = ""

                //Save score only if logged in for now
                if(mAuth!!.currentUser != null){

                    val date = Date()

                    var dbScores: CollectionReference = db!!.collection("scores")

                    var scoreToSave= Score(mAuth!!.currentUser!!.uid, mAuth!!.currentUser!!.displayName!!, date, score)

                    dbScores.add(scoreToSave)
                        .addOnSuccessListener { }
                        .addOnFailureListener { e -> Toast.makeText(this@QuizActivity, "Error occurred. Score not saved.", Toast.LENGTH_LONG).show() }

                }else{
                    Toast.makeText(this@QuizActivity, "Must be logged in to save score.", Toast.LENGTH_LONG).show()
                    weeklyRank = "Log in to track your scores!"
                }

                val intent = Intent(baseContext, EndQuizActivity::class.java)
                intent.putExtra("score", score)
                startActivity(intent)

            }
        }

        timer!!.start()
    }

    override fun onBackPressed() {

        timer!!.cancel()

        val menuActivity = Intent(this, MainActivity::class.java)
        startActivity(menuActivity)
        finish()

    }

    fun initLanguageObjs():ArrayList<Language>{

        val langList:ArrayList<Language> = ArrayList()

        langList.add(Language("4DOS Batch", "_4dos_batch", 1))
        langList.add(Language("4GL", "_4gl", 1))
        langList.add(Language("4Test", "_4test", 1))
        langList.add(Language("A+", "a_plus", 1))
        langList.add(Language("Aassembler-6502", "aassembler_6502", 1))
        langList.add(Language("Abap - SAP AG", "abap_sap_ag", 1))
        langList.add(Language("ABC", "abc", 1))
        langList.add(Language("ActionScript 3", "actionscript_3", 1))
        langList.add(Language("ActionScript", "actionscript", 1))
        langList.add(Language("Ada", "ada", 1))
        langList.add(Language("Adobe Flex MXML", "adobe_flex_mxml", 1))
        langList.add(Language("Algol 60", "algol_60", 1))
        langList.add(Language("Algol 68", "algol_68", 1))
        langList.add(Language("Alma-0", "alma_0", 1))
        langList.add(Language("AmigaE", "amigae", 1))
        langList.add(Language("AMX NetLinx", "amx_netlinx", 1))
        langList.add(Language("Apl", "apl", 1))
        langList.add(Language("AppleScript", "applescript", 1))
        langList.add(Language("Ascii", "ascii", 1))
        langList.add(Language("ASP.NET", "asp_net", 2))
        langList.add(Language("ASP", "asp", 1))
        langList.add(Language("AspectJ", "aspectj", 1))
        langList.add(Language("Assembler - Intel x86; Dos; Tasm", "assembler_intel_x86_dos_tasm", 1))
        langList.add(Language("Assembler - Intel x86; Linux", "assembler_intel_x86_linux", 1))
        langList.add(Language("Assembler 68000", "assembler_68000", 1))
        langList.add(Language("AutoHotkey", "autohotkey", 1))
        langList.add(Language("Autoit", "autoit", 1))
        langList.add(Language("Avenue - Scripting language for ArcView GIS", "avenue", 1))
        langList.add(Language("AviSynth", "avisynth", 1))
        langList.add(Language("Awk", "awk", 1))
        langList.add(Language("B", "b", 1))
        langList.add(Language("Baan Tools", "baan_tools", 1))
        langList.add(Language("Ball", "ball", 1))
        langList.add(Language("Bash", "bash", 2))
        langList.add(Language("Basic - StarOffice OpenOffice", "basic_staroffice_openoffice", 1))
        langList.add(Language("Basic", "basic", 1))
        langList.add(Language("bc", "bc", 1))
        langList.add(Language("Bcpl", "bcpl", 1))
        langList.add(Language("Beta", "beta", 1))
        langList.add(Language("BITGGAL AgileDog", "bitggal_agiledog", 1))
        langList.add(Language("BITGGAL Jihwaja", "bitggal_jihwaja", 1))
        langList.add(Language("Bliss", "bliss", 1))
        langList.add(Language("BlitzBasic", "blitzbasic", 1))
        langList.add(Language("Boo", "boo", 1))
        langList.add(Language("Burning Sand 2", "burning_sand_2", 1))
        langList.add(Language("C#", "c_sharp", 2))
        langList.add(Language("C", "c", 2))
        langList.add(Language("C++", "c_plusplus", 2))
        langList.add(Language("Cache Server Pages (CSP)", "csp", 1))
        langList.add(Language("Caml light", "caml_light", 1))
        langList.add(Language("CCL", "ccl", 1))
        langList.add(Language("Ceylon", "ceylon", 1))
        langList.add(Language("Ch", "ch", 1))
        langList.add(Language("Chapel", "chapel", 1))
        langList.add(Language("CHILL", "chill", 1))
        langList.add(Language("Chrome", "chrome", 1))
        langList.add(Language("Chuck", "chuck", 1))
        langList.add(Language("Cil", "cil", 1))
        langList.add(Language("Clarion", "clarion", 1))
        langList.add(Language("Clean", "clean", 1))
        langList.add(Language("Clipper", "clipper", 1))
        langList.add(Language("Clist", "clist", 1))
        langList.add(Language("Clu", "clu", 1))
        langList.add(Language("Cobol", "cobol", 2))
        langList.add(Language("ColdFusion", "coldfusion", 1))
        langList.add(Language("Comal", "comal", 1))
        langList.add(Language("Common Lisp", "common_lisp", 2))
        langList.add(Language("ConTeXt", "context", 1))
        langList.add(Language("Crystal", "crystal", 2))
        langList.add(Language("Curl", "curl", 1))
        langList.add(Language("D", "d", 1))
        langList.add(Language("D++", "d_plusplus", 1))
        langList.add(Language("DarkBasic", "darkbasic", 1))
        langList.add(Language("Dart", "dart", 2))
        langList.add(Language("Dataflex", "dataflex", 1))
        langList.add(Language("dBase", "dbase", 1))
        langList.add(Language("Dcl batch", "dcl_batch", 1))
        langList.add(Language("Delphi; Kylix", "delphi_kylix", 1))
        langList.add(Language("DIV", "div", 1))
        langList.add(Language("Doll", "doll", 1))
        langList.add(Language("Dream Maker", "dream_maker", 1))
        langList.add(Language("Dylan", "dylan", 1))
        langList.add(Language("EAS 0.0.1.X", "eas", 1))
        langList.add(Language("Ed and Ex", "edex", 1))
        langList.add(Language("Eiffel", "eiffel", 1))
        langList.add(Language("Elan", "elan", 1))
        langList.add(Language("Elixir", "elixir", 2))
        langList.add(Language("Elm", "elm", 1))
        langList.add(Language("Erlang", "erlang", 1))
        langList.add(Language("Euphoria", "euphoria", 1))
        langList.add(Language("F#", "f_sharp", 2))
        langList.add(Language("Factor", "factor", 1))
        langList.add(Language("Ferite", "ferite", 1))
        langList.add(Language("filePro", "file_pro", 1))
        langList.add(Language("Fjolnir", "fjolnir", 1))
        langList.add(Language("Focal", "focal", 1))
        langList.add(Language("Focus", "focus", 1))
        langList.add(Language("Forth", "forth", 1))
        langList.add(Language("Fortran", "fortran", 2))
        langList.add(Language("FreeBasic", "free_basic", 1))
        langList.add(Language("Fril", "fril", 1))
        langList.add(Language("Frink", "frink", 1))
        langList.add(Language("Gambas", "gambas", 1))
        langList.add(Language("Game Maker", "game_maker", 1))
        langList.add(Language("GEMBase 4GL", "gembase_4gl", 1))
        langList.add(Language("Go Language", "go_lang", 2))
        langList.add(Language("GraalScript 1", "graalscript_1", 1))
        langList.add(Language("GraalScript 2", "graalscript_2", 1))
        langList.add(Language("Groovy", "groovy", 2))
        langList.add(Language("Hack", "hack", 1))
        langList.add(Language("Haskell", "haskell", 2))
        langList.add(Language("haXe", "haxe", 1))
        langList.add(Language("Heron", "heron", 1))
        langList.add(Language("HLA (High Level Assembly)", "hla", 1))
        langList.add(Language("HP 33s", "hp_33s", 1))
        langList.add(Language("HP-41; HP-42S", "hp_41_42s", 1))
        langList.add(Language("HTML", "html", 2))
        langList.add(Language("HyperTalk", "hyper_talk", 1))
        langList.add(Language("Icon", "icon", 1))
        langList.add(Language("IDL", "idl", 1))
        langList.add(Language("Inform 5-6", "inform_5_6", 1))
        langList.add(Language("Inform 7", "inform_7", 1))
        langList.add(Language("Io", "io", 1))
        langList.add(Language("Iptscrae", "iptscrae", 1))
        langList.add(Language("J", "j", 1))
        langList.add(Language("Jal", "jal", 1))
        langList.add(Language("Java", "java", 2))
        langList.add(Language("JavaScript", "javascript", 2))
        langList.add(Language("Joy", "joy", 1))
        langList.add(Language("JSP", "jsp", 1))
        langList.add(Language("Julia", "julia", 1))
        langList.add(Language("K", "k", 1))
        langList.add(Language("Kogut", "kogut", 1))
        langList.add(Language("Kotlin", "kotlin", 2))
        langList.add(Language("KPL - Kids Programming Language", "kpl", 1))
        langList.add(Language("Lasso", "lasso", 1))
        langList.add(Language("LaTeX", "latex", 1))
        langList.add(Language("Liberty BASIC", "liberty_basic", 1))
        langList.add(Language("Limbo", "limbo", 1))
        langList.add(Language("Linden Scripting Language", "linden", 1))
        langList.add(Language("Lingo (Macromedia Director scripting language)", "lingo", 1))
        langList.add(Language("Linotte", "linotte", 1))
        langList.add(Language("Lisaac", "lisaac", 1))
        langList.add(Language("Lua", "lua", 2))
        langList.add(Language("LuaPSP", "luapsp", 1))
        langList.add(Language("M (MUMPS)", "m_mumps", 1))
        langList.add(Language("M4", "m4", 1))
        langList.add(Language("Macsyma; Maxima", "macsyma_maxima", 1))
        langList.add(Language("Maple", "maple", 1))
        langList.add(Language("Mathematica", "mathematica", 1))
        langList.add(Language("Matlab", "matlab", 2))
        langList.add(Language("Maude", "maude", 1))
        langList.add(Language("Max", "max", 1))
        langList.add(Language("Maya Embedded Language", "maya_embedded_language", 1))
        langList.add(Language("mlrc Script", "mlrc_script", 1))
        langList.add(Language("Model 204", "model_204", 1))
        langList.add(Language("Modula-2", "modula_2", 1))
        langList.add(Language("Moo", "moo", 1))
        langList.add(Language("Mouse", "mouse", 1))
        langList.add(Language("Ms-Dos batch", "ms_dos_batch", 1))
        langList.add(Language("Muf", "muf", 1))
        langList.add(Language("Natural", "natural", 1))
        langList.add(Language("Nemerle", "nemerle", 1))
        langList.add(Language("NewtonScript", "newton_script", 1))
        langList.add(Language("Nice", "nice", 1))
        langList.add(Language("NOMAD (4GL)", "nomad_4gl", 1))
        langList.add(Language("NSIS", "nsis", 1))
        langList.add(Language("Oberon", "oberon", 1))
        langList.add(Language("OCaml", "ocaml", 1))
        langList.add(Language("Occam", "occam", 1))
        langList.add(Language("Octave", "octave", 1))
        langList.add(Language("OPL", "opl", 1))
        langList.add(Language("Ops5", "ops5", 1))
        langList.add(Language("Ops83", "ops83", 1))
        langList.add(Language("Oz", "oz", 1))
        langList.add(Language("Parrot assembly language", "parrot", 1))
        langList.add(Language("Parrot intermediate representation", "parrot_ir", 1))
        langList.add(Language("Pascal", "pascal", 2))
        langList.add(Language("PAWN", "pawn", 1))
        langList.add(Language("PBasic", "pbasic", 1))
        langList.add(Language("Perl 6", "perl_6", 1))
        langList.add(Language("Perl", "perl", 2))
        langList.add(Language("PHP", "php", 2))
        langList.add(Language("Pike", "pike", 1))
        langList.add(Language("Pilot", "pilot", 1))
        langList.add(Language("PL/SQL", "pl_sql", 1))
        langList.add(Language("Pl-I", "pl_i", 1))
        langList.add(Language("Pop 11", "pop_11", 1))
        langList.add(Language("PostScript", "postscript", 1))
        langList.add(Language("Pov-Ray", "pov_ray", 1))
        langList.add(Language("Processing", "processing", 1))
        langList.add(Language("Profan", "profan", 1))
        langList.add(Language("Progress", "progress", 1))
        langList.add(Language("Prolog", "prolog", 2))
        langList.add(Language("Protocol Buffers", "protocol_buffers", 1))
        langList.add(Language("Pure Data", "pure_data", 1))
        langList.add(Language("PureBasic", "pure_basic", 1))
        langList.add(Language("Python", "python", 1))
        langList.add(Language("QuakeC", "quakec", 1))
        langList.add(Language("QuickBasic", "quick_basic", 1))
        langList.add(Language("R", "r", 2))
        langList.add(Language("Ratfor", "ratfor", 1))
        langList.add(Language("RealBasic", "real_basic", 1))
        langList.add(Language("Rebol", "rebol", 1))
        langList.add(Language("Red", "red", 1))
        langList.add(Language("Refal", "refal", 1))
        langList.add(Language("Rexx", "rexx", 1))
        langList.add(Language("Ring", "ring", 1))
        langList.add(Language("Robotic (MegaZeux)", "robotic", 1))
        langList.add(Language("Rpg Code", "rpg_code", 1))
        langList.add(Language("RPG", "rpg", 1))
        langList.add(Language("RPL (HP Calculators)", "rpl", 1))
        langList.add(Language("Rsl", "rsl", 1))
        langList.add(Language("RT Assembler", "rt_assembler", 1))
        langList.add(Language("Rtf", "rtf", 1))
        langList.add(Language("RTML", "rtml", 1))
        langList.add(Language("Ruby", "ruby", 2))
        langList.add(Language("Rust", "rust", 2))
        langList.add(Language("S", "s", 1))
        langList.add(Language("Sas", "sas", 1))
        langList.add(Language("Sather", "sather", 1))
        langList.add(Language("Scala", "scala", 2))
        langList.add(Language("SCAR", "scar", 1))
        langList.add(Language("Scheme", "scheme", 2))
        langList.add(Language("Scriptol", "scriptol", 1))
        langList.add(Language("sed", "sed", 1))
        langList.add(Language("Seed7", "seed7", 1))
        langList.add(Language("Self", "self", 1))
        langList.add(Language("Setl", "setl", 1))
        langList.add(Language("Simula", "simula", 1))
        langList.add(Language("S-Lang", "s_lang", 1))
        langList.add(Language("Smalltalk", "small_talk", 2))
        langList.add(Language("Smil", "smil", 1))
        langList.add(Language("Sml", "sml", 1))
        langList.add(Language("Snobol", "snobol", 1))
        langList.add(Language("Span", "span", 1))
        langList.add(Language("Spitbol", "spitbol", 1))
        langList.add(Language("SPSS Syntax", "spss_syntax", 1))
        langList.add(Language("SQL", "sql", 2))
        langList.add(Language("SSPL", "sspl", 1))
        langList.add(Language("Starlet", "starlet", 1))
        langList.add(Language("STATA", "stata", 1))
        langList.add(Language("SuperCollider", "super_collider", 1))
        langList.add(Language("Swift", "swift", 2))
        langList.add(Language("T programming language", "t_lang", 1))
        langList.add(Language("Tcl (Tool command language)", "tcl", 1))
        langList.add(Language("Teco", "teco", 1))
        langList.add(Language("Template Toolkit", "template_toolkit", 1))
        langList.add(Language("TeX", "tex", 1))
        langList.add(Language("TI-BASIC", "ti_basic", 2))
        langList.add(Language("Tk", "tk", 1))
        langList.add(Language("TOM (rewriting language)", "tom", 1))
        langList.add(Language("TSQL", "tsql", 1))
        langList.add(Language("TTCN-3", "ttcn_3", 1))
        langList.add(Language("Turing", "turing", 1))
        langList.add(Language("Ubercode", "ubercode", 1))
        langList.add(Language("Uniface", "uniface", 1))
        langList.add(Language("Unix shell", "unix_shell", 1))
        langList.add(Language("UnrealScript", "unreal_script", 1))
        langList.add(Language("VBA", "vba", 2))
        langList.add(Language("Verilog", "verilog", 1))
        langList.add(Language("VHDL", "vhdl", 1))
        langList.add(Language("Visual Basic .Net 2003", "visual_basic_net_2003", 1))
        langList.add(Language("Visual Basic .Net", "visual_basic_net", 1))
        langList.add(Language("Visual Basic Script", "visual_basic_script", 1))
        langList.add(Language("Visual Basic", "visual_basic", 1))
        langList.add(Language("Visual DialogScript", "visual_dialogscript", 1))
        langList.add(Language("Visual Prolog console program", "visual_prolog_console_program", 1))
        langList.add(Language("Vmrl", "vmrl", 1))
        langList.add(Language("Vms", "vms", 1))
        langList.add(Language("Windows PowerShell", "windows_powershell", 1))
        langList.add(Language("X++", "x_plusplus", 1))
        langList.add(Language("XAML-WPF", "xaml_wpf", 1))
        langList.add(Language("XL", "xl", 1))
        langList.add(Language("Xml", "xml", 2))
        langList.add(Language("XQuery", "x_query", 1))
        langList.add(Language("XS programming language", "xs_lang", 1))
        langList.add(Language("XSLT", "xslt", 1))
        langList.add(Language("Yorick", "yorick", 1))

        for(index in 0 until langList.size){

            //Add higher priority languages twice
            if(langList[index].priority == 2){
                langList.add(langList[index])
                langList.add(langList[index])
            }

        }

        return langList

    }

}

class Language (val name:String, val imgFileName:String, val priority:Int)