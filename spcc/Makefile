exp4:
	@echo "Lab4: Generate ICG from three address code"
	@java ThreeAddressCodeGenerator.java tac.txt

exp5/1:
	@echo "Lab5: Count Number of vowels and consonants"
	lex ./vowel_consonant.l
	gcc lex.yy.c
	./a.out

exp5/2:
	@echo "Lab5: Characters Words Spaces End of Line"
	lex ./words_spaces_characters_eol.l
	gcc lex.yy.c
	./a.out ./test.txt

exp5/3:
	@echo "Lab5: Count Number postive and negative integers and decimals"
	lex ./pos_neg_int_dec.l
	gcc lex.yy.c
	./a.out

exp5/4:
	@echo "Lab5: Remove comments from a file" 
	lex ./comments.l
	gcc lex.yy.c
	./a.out ./fib.c uncommented_fib.c

exp5/5:
	@echo -e "Lab5: Count the number of ids keywords, opcode \nand special symbols in a c file" 
	lex ./id_keyword_op_special_sym.l
	gcc lex.yy.c
	./a.out ./fib.c
