/**
 * Ruby syntax v 1.0 
 * 
 * v1.0 by Patrice De Saint Steban (2007/01/03)
 *   
**/
editAreaLoader.load_syntax["groovy"] = {
	'COMMENT_SINGLE' : {1 : '//'}
	,'COMMENT_MULTI' : {'/*' : '*/'}
	,'QUOTEMARKS' : {1: "'", 2: '"'}
	,'KEYWORD_CASE_SENSITIVE' : true
	,'KEYWORDS' : {
        'reserved':[
            "instanceof","implements","synchronized","const","package","return","throw","def","threadsafe",
            "class","throws","strictfp","super","transient","native","interface",
            "final","volatile","as","assert","enum","extends", "this","static","abstract",
            "import","void"
        ],
        'constants' : [
			'null', 'false', 'true'
		]
		,'types' : [
			'String', "boolean", 'char', 'const', 'double', 'float', 'int', 'long', 'short', 'void', 'volatile','byte'
		]
		,'visibility' : [
			'private', 'public', 'protected'
		]
		,'statements' : [
			'do', "in",'else', 'for', 'goto', 'if', 'switch', 'while',"try","catch"
		],
        'keywords':["break","new","goto","case","finally","continue","default"]
    }
	,'OPERATORS' :[
		'+', '-', '/', '*', '=', '<', '>', '%', '!', '&', ';', '?', '`', ':', ','
	]
	,'DELIMITERS' :[
		'(', ')', '[', ']', '{', '}'
	]
	,'REGEXPS' : {
		'constants' : {
			'search' : '()([A-Z]\\w*)()'
			,'class' : 'constants'
			,'modifiers' : 'g'
			,'execute' : 'before' 
		}
		,'variables' : {
			'search' : '()([\$\@\%]+\\w+)()'
			,'class' : 'variables'
			,'modifiers' : 'g'
			,'execute' : 'before' 
		}
		,'numbers' : {
			'search' : '()(-?[0-9]+)()'
			,'class' : 'numbers'
			,'modifiers' : 'g'
			,'execute' : 'before' 
		}
		,'symbols' : {
			'search' : '()(:\\w+)()'
			,'class' : 'symbols'
			,'modifiers' : 'g'
			,'execute' : 'before'
		}
	}
	,'STYLES' : {
		'COMMENTS': 'color: #AAAAAA;'
		,'QUOTESMARKS': 'color: #6381F8;'
		,'KEYWORDS' : {
			'reserved' : 'font-weight: bold; color: #0000FF;'
			,'types' : 'font-weight: bold; color: #0000EE;'
			,'statements' : 'font-weight: bold; color: #60CA00;'
			,'keywords' : 'font-weight: bold; color: #48BDDF;',
            'visibility': 'font-weight: bold; color: #48BDDF;'
        }
		,'OPERATORS' : 'color: #FF00FF;'
		,'DELIMITERS' : 'color: #0038E1;'
	}
};
