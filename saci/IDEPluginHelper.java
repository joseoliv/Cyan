package saci;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import ast.CompilationUnit;
import ast.Annotation;
import ast.FieldDec;
import ast.ObjectDec;
import ast.Prototype;
import lexer.HighlightColor;
import lexer.Symbol;
import lexer.SymbolIdent;
import meta.CyanMetaobject;
import meta.Tuple2;
import meta.Tuple4;

/**
 * class with static methods to link the compiler to the IDE
   @author jose
 */
public class IDEPluginHelper {

	/**
	 * Return a list of tuples. Each tuple (color number, line number, column number, size). <br>
	 * The characters starting at line number, column number till column number + size - 1
	 * should be highlighted in color "color number".
	   @param saci
	   @return
	 */
	static public List<Tuple4<Integer, Integer, Integer, Integer>>  getColorList(Saci saci) {
		List<Tuple4<Integer, Integer, Integer, Integer>> colorList = new ArrayList<>();

		HashSet<String> ivSet = new HashSet<>();
		CompilationUnit compUnit = saci.getLastCompilationUnitParsed();
		ObjectDec proto = null;
		if ( compUnit != null ) {
			Prototype pu = compUnit.getPublicPrototype();
			if ( pu != null && pu instanceof ObjectDec ) {
				proto = (ObjectDec ) pu;
				for ( FieldDec iv : proto.getFieldList() ) {
					ivSet.add(iv.getName());
				}
			}

		}


		for (int i = 0; i < saci.getSizeSymbolList(); ++i) {
			Symbol sym = saci.getSymbolList()[i];

			Annotation annotation = sym.getCyanAnnotation();
			if ( annotation != null ) {
				/*
				 * symbol represents a metaobject annotation
				 */
				CyanMetaobject cyanMetaobject = annotation.getCyanMetaobject();
				List<Tuple4<Integer, Integer, Integer, Integer>> moColorList = cyanMetaobject.getColorList();
				if ( moColorList != null && moColorList.size() > 0 ) {
					int baseLine = annotation.getFirstSymbol().getLineNumber();
					for ( Tuple4<Integer, Integer, Integer, Integer> t : moColorList ) {
						/*
						 * baseLine must be added because the metaobject uses line numbers relative to the start of
						 * the metaobject annotation.
						 */
						colorList.add(new Tuple4<Integer, Integer, Integer, Integer>(
								t.f1, t.f2 + baseLine, t.f3, t.f4));
					}
				}
			}
			else if ( sym instanceof SymbolIdent ) {
				SymbolIdent si = (SymbolIdent ) sym;
				if ( ivSet.contains(si.getSymbolString()) ) {
					// identifier is a field. Use field color
					colorList.add(new Tuple4<Integer, Integer, Integer, Integer>(
							HighlightColor.field, sym.getLineNumber(), sym.getColumnNumber(), sym.getSymbolString().length()));
				}
			}
			else {
				// not a metaobject annotation
				colorList.add(new Tuple4<Integer, Integer, Integer, Integer>(
						sym.getColor(), sym.getLineNumber(), sym.getColumnNumber(), sym.getSymbolString().length()));
			}
		}

		return colorList;
	}

	/**
	 * This method should be called by a IDE plugin to show a list of code
	 * completion alternatives when the cursor is in character at position <code>offset</code> of
	 * the current source file being edited.
	 * The return value is a list of tuples, each one is composed of an option and what should be
	 * inserted in the text being edited if this option is chosen. For example,
	 * the first tuple element could be<br>
	 * <code> substring(int beginIndex, int endIndex)</code><br>
	 * and the second could be<br>
	 * <code> substring(beginIndex, endIndex)</code><br>
	 */
	static public List<Tuple2<String, String>> getCodeCompletionAlternatives(Saci saci, int offset) {

		// List<Tuple2<String, String>> list = null;

		for (int i = 0; i < saci.getSizeSymbolList(); ++i) {
			Symbol sym = saci.getSymbolList()[i];

			Annotation annot = sym.getCyanAnnotation();
			if ( annot != null ) {
				/*
				 * symbol represents a metaobject annotation
				 */
				int annotStartOffset = annot.getFirstSymbol().getOffset();
				int annotEndOffset = annot.getLastSymbol().getOffset() + annot.getLastSymbol().getSymbolString().length();
				if ( annotStartOffset <= offset && offset < annotEndOffset ) {
					// offset is inside annotation
					return annot.getCyanMetaobject().getCodeCompletionAlternatives(offset - annotStartOffset);
				}
				else {
					return null;
				}
			}
			if ( offset < sym.getOffset() ) { return null; }
		}

		return null;
	}
}
