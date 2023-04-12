top@{ inputs, ... }: {
  imports = [
    ./thirdparty/gryphon/flake-module.nix
    ./thirdparty/objc2swift/flake-module.nix
  ];
  flake = { };
  perSystem = { pkgs, lib, stdenv, self', webDriverAgentSrc, ... }: {
    packages.ipredicateKt =
        let
          gryphon = self'.packages.gryphon;
          objc2swift = self'.packages.objc2swift;
          gnuStepBaseSrc = top.inputs.gnuStepBaseSrc;
          webDriverAgentSrc = top.inputs.webDriverAgentSrc;
          stdenv = pkgs.stdenv;
        in
        let predicateConcatenated = stdenv.mkDerivation {
          name = "GSPredicate.swift";
          src = ./.;
          dontBuild = true;
          installPhase = ''
                 set -ex
                 mkdir -p $out

                 cat ${gnuStepBaseSrc}/Headers/GNUstepBase/GSVersionMacros.h ${gnuStepBaseSrc}/Headers/GNUstepBase/GNUstep.h \
                 ${gnuStepBaseSrc}/Headers/Foundation/NSObjCRuntime.h ${gnuStepBaseSrc}/Headers/Foundation/NSException.h \
                 ${gnuStepBaseSrc}/Headers/Foundation/NSPredicate.h \
                 ${gnuStepBaseSrc}/Headers/Foundation/NSScanner.h ${gnuStepBaseSrc}/Source/NSScanner.m \
                 ${gnuStepBaseSrc}/Headers/Foundation/NSComparisonPredicate.h ${gnuStepBaseSrc}/Headers/Foundation/NSCompoundPredicate.h \
                 ${gnuStepBaseSrc}/Source/NSPredicate.m \
                 >> $out/GSPredicate.m
          '';
        }; in
        let predicateSwift = stdenv.mkDerivation {
          inherit predicateConcatenated;
          name = "GSPredicate.swift";
          src = ./.;
          dontBuild = true;
          buildInputs = [ pkgs.perl pkgs.pcre2 pkgs.python38Packages.pcpp objc2swift ];
          installPhase = ''
                 set -ex
                 mkdir -p $out
                 echo > $out/GSPredicate.m
                 echo '#define OS_API_VERSION(v, m) 1' >> $out/GSPredicate.m
                 echo '#define GS_USE_ICU 1' >> $out/GSPredicate.m
                 echo '#define BASE_NATIVE_OBJC_EXCEPTIONS 1' >> $out/GSPredicate.m
                 echo '#define OBJC_ZEROCOST_EXCEPTIONS 1' >> $out/GSPredicate.m
                 echo '#define NS_FORMAT_FUNCTION(a, b)' >> $out/GSPredicate.m
                 echo '#define DEFINE_BLOCK_TYPE(a, b, c, d)' >> $out/GSPredicate.m
                 echo '#define YES YES' >> $out/GSPredicate.m
                 echo '#define NO NO' >> $out/GSPredicate.m
                 echo '#define nil nil' >> $out/GSPredicate.m
                 echo '#define UINT_MAX 0xffffffff' >> $out/GSPredicate.m
                 echo '#define __LONG_LONG_MAX__ 9223372036854775807ULL' >> $out/GSPredicate.m
       
                 cat $predicateConcatenated/* >> $out/GSPredicate.m
       
                 perl -i -0pe 's|__has_feature\s*\(\s*objc_arc\s*\)|1|g' $out/GSPredicate.m
                 perl -i -0pe 's|__has_extension\s*\(\s*objc_arc\s*\)|1|g' $out/GSPredicate.m
                 perl -i -0pe 's|__has_feature\s*\(\s*objc_generics\s*\)|1|g' $out/GSPredicate.m
                 perl -i -0pe 's|__has_extension\s*\(\s*objc_generics\s*\)|1|g' $out/GSPredicate.m
                 perl -i -0pe 's|__unsafe_unretained||g' $out/GSPredicate.m
                 perl -i -0pe 's|#include\s+.GNUstepBase/\S+||g' $out/GSPredicate.m
                 perl -i -0pe 's|#if\s+1([^#]+GSBlockPredicate[^#]+#endif)|/*#if 0$1*/|g' $out/GSPredicate.m
                 perl -i -0pe 's|typedef enum _\w+([\S\s]+?})\s*(\w+)|enum $1; typedef NSUInteger $2|g' $out/GSPredicate.m
       
                 pcpp -E -nostdinc --passthru-unfound-includes $out/GSPredicate.m > $out/GSPredicate1.m
                 rm $out/GSPredicate.m
                 mv $out/GSPredicate1.m $out/GSPredicate.m
                 
                 sed -Ei 's|\[\(([^)]+)\) copy\]|[\1 copy]|g' $out/GSPredicate.m
                 perl -i -0pe 's|(#\s+\d+.+)|// $1|g' $out/GSPredicate.m
       
                 mkdir home
                 HOME=$PWD/home objc2swift --init macos
                 HOME=$PWD/home objc2swift --skip-header $out/GSPredicate.m -o $out/GSPredicate1.swift
                 rm $out/GSPredicate.m

                 echo 'extension NSObject : CVarArg { public var _cVarArgEncoding: [Int] { return [1, 1]; } }' >> $out/GSPredicate.swift
                 echo 'struct Selector {}' >> $out/GSPredicate.swift
                 echo 'let NSDecimalSeparator = "\""' >> $out/GSPredicate.swift
                 echo 'func isEqual(_ lhs: AnyObject?, _ rhs: AnyObject?) -> Bool { return false; }' >> $out/GSPredicate.swift
                 echo 'func notIsEqual(_ lhs: AnyObject?, _ rhs: AnyObject?) -> Bool { return true; }' >> $out/GSPredicate.swift
                 echo 'import Foundation' >> $out/GSPredicate.swift
                 echo 'enum NSComparisonResult' >> $out/GSPredicate.swift

                 sed '1,/typealias NSComparisonResult = Int enum/d' $out/GSPredicate1.swift >> $out/GSPredicate.swift
                 rm $out/GSPredicate1.swift

                 perl -i -0pe 's|enum\s*(\{[\S\s]+?\}\s*)typealias\s*(\w+)\s*=\s*(\w+)|enum $2: $3$1|g' $out/GSPredicate.swift
                 for ENUM_NAME in $(pcre2grep -o 'enum\s+\K\w+' $out/GSPredicate.swift); do
                   for ENUM_CONSTANT in $(pcre2grep -Mo "enum\\s+''${ENUM_NAME}\\W[^{]+{\\K[^}]+" $out/GSPredicate.swift | pcre2grep -o1 'case\s*(\w+)'); do
                     perl -i -0pe "s|(\W)''${ENUM_CONSTANT}(\W)|\$1''${ENUM_NAME}.''${ENUM_CONSTANT}\$2|g" $out/GSPredicate.swift
                   done
                 done
                 perl -i -0pe 's|(enum\s+[^{}]+\{[^{}]+\})|my $replacement=$1; $replacement=~s!case\s+\K\w+\.!!g; $replacement |ge' $out/GSPredicate.swift
                 perl -i -0pe 's|(\:\[Double\]! = )\{\s*([^\}]+)\s*\}|$1\[$2\]|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?<=\n)(\s+)(class\s+func\s+predicateWithFormat.+va_list[\S\s]+?\1})|$1/*$2*/|g' $out/GSPredicate.swift
                 perl -i -0pe  's|\:UInt\s*=\s*(.+)\.count\(\)| = $1.count|g' $out/GSPredicate.swift
                 perl -i -0pe  's|\:unsigned\s*=\s*(.+)\.count\(\)| = $1.count|g' $out/GSPredicate.swift
                 perl -i -0pe 's|:UInt = (\w+)\.length\(\)| = $1.length|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(:\s*)(\w+)(! = .+?.objectAtIndex\([^)]+\))|$1$2$3 as! $2|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(:\s*)(\w+)(! = .+?.objectForKey\([^)]+\))|$1$2$3 as! $2|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(:\s*)(\w+)(! = .+?.nextObject\(\))|$1$2$3 as! $2|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(.+\Windex)\+\+(\W.+)|$1$2; index += 1|g' $out/GSPredicate.swift
                 sed -Ei  's|private var _subs\:\[AnyObject\]|var _subs\:NSArray|g' $out/GSPredicate.swift
                 sed -Ei  's|private var _argc\:UInt|private var _argc\:Int|g' $out/GSPredicate.swift
                 perl -i -0pe 's|let\s+dst:unichar!\s+=\s+&u|let dst:[unichar] = [u]|g' $out/GSPredicate.swift
                 perl -i -0pe 's|\[AnyObject\]|NSArray|g' $out/GSPredicate.swift
                 perl -i -0pe 's|:AnyClass\s*=\s*object_getClass\(| = \(|g' $out/GSPredicate.swift
                 sed -Ei  's|object_getClass\(|\(|g' $out/GSPredicate.swift
                 perl -i -0pe 's|GSObjCIsKindOf\(\s*([^,]+)\s*,[^,)]*String[^,)]+\)|\($1 != nil && $1 is String\)|g' $out/GSPredicate.swift
                 perl -i -0pe 's|max\s*([\/%])\s*radix|max $1 UInt64(radix)|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(\d)U?LL|$1|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?:override\s+)?func\s+\w+Zone[^{]+\{[^}]+}||g' $out/GSPredicate.swift
                 sed -Ei  's|\.doubleValueFor\(|\.doubleValueFor\(value: |g' $out/GSPredicate.swift
                 sed -Ei  's|\.expressionValueWithObject\(|\.expressionValueWithObject\(object: |g' $out/GSPredicate.swift
                 sed -Ei  's|\._evaluateLeftValue\(|\._evaluateLeftValue\(leftResult: |g' $out/GSPredicate.swift
                 sed -Ei  's|\._enum\(|\._enum\(expressions: |g' $out/GSPredicate.swift
                 perl -i -0pe 's|index\:UInt\s*\=\s*0|index \= 0|g' $out/GSPredicate.swift
                 perl -i -0pe 's|\:NSScanner\! \= self\.scannerWithString\(aString\)|:NSScanner! = self.scannerWithString(aString: aString) as! NSScanner|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(let\s+right\:NSCompoundPredicate\!\s+\=\s+)r|$1(r as! NSCompoundPredicate)|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(let\s+left\:NSCompoundPredicate\!\s+\=\s+)l|$1(l as! NSCompoundPredicate)|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(var type\:GSPredicateOperatorType \= )0|$1GSPredicateOperatorType.NSLessThanPredicateOperatorType|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(\:NSArray\!\s+\=\s+exp\.constantValue\(\))(\s*?\n)|$1 as! NSArray$2|g' $out/GSPredicate.swift
                 perl -i -0pe 's#(\(nil \=\= o\)|nil \=\= o) \? 0\.0 \: o\.doubleValue\(\)#o?.doubleValue ?? 0.0#g' $out/GSPredicate.swift
                 perl -i -0pe 's|\(\s*([^(\s]+)\s+as\!\s+ivars\)\-\>_contents\.[cu]\[([^\]]+)\]|$1\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/GSPredicate.swift
                 perl -i -0pe 's|myGetC\(\(([^)]+string\[[^]]+\])\)\)|\($1\)|g' $out/GSPredicate.swift
                 perl -i -0pe 's|_isUnicode\s*\?\s*([^:]+:[^:]+)\s*:\s*\1|$1|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?<=\W)unichar(?=\W)|UniChar|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?<=\W)SEL(?=\W)|Selector|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(\:\s*UniChar\s+\=\s+)0|$1"\\0"|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(\s+\=\s+string\.)length(?:\(\))?|$1utf16.count|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(\s+\=\s+regex\.)length(?:\(\))?|$1utf16.count|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?<=\=)\s+([^.]+)\.characterAtIndex\(([^)]+)\)(?!\))| $1\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(?<=\=)\s+(.+?)\.characterAtIndex\(([^)]+\(\))\)(?!\))| $1\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/GSPredicate.swift
                 perl -i -0pe 's|\(\s*1U?L?\s*\<\<\s*0\s*\)|1|g' $out/GSPredicate.swift
                 perl -i -0pe 's|\(\s*1U?L?\s*\<\<\s*1\s*\)|2|g' $out/GSPredicate.swift
                 perl -i -0pe 's|\(\s*1U?L?\s*\<\<\s*4\s*\)|16|g' $out/GSPredicate.swift
                 perl -i -0pe 's#(Name|String)\.length\(\)#$1.utf16.count#g' $out/GSPredicate.swift
                 perl -i -0pe 's#(?<! )var\s+\S+\@convention\(c\)\s+\S+##g' $out/GSPredicate.swift
                 perl -i -0pe 's#(:\s*\S+)\!#$1#g' $out/GSPredicate.swift
                 perl -i -0pe 's#(->\s*\S+)\!#$1#g' $out/GSPredicate.swift
                 perl -i -0pe 's#(:\s*inout\s*\S+)\!#$1#g' $out/GSPredicate.swift
                 perl -i -0pe 's|#line\s+\d+.+?\r?\n||g' $out/GSPredicate.swift
                 perl -i -0pe 's|//#import.+?\r?\n||g' $out/GSPredicate.swift
                 perl -i -0pe 's|\$\(IncludeDirective\)\r?\n||g' $out/GSPredicate.swift
                 perl -i -0pe 's|/nix/store/\w+-[\w.]+/|prefix/|g' $out/GSPredicate.swift

                 patch --merge --ignore-whitespace --verbose -o $out/GSPredicate2.swift -p2 $out/GSPredicate.swift GSPredicate.swift.patch
                 rm $out/GSPredicate.swift
                 mv $out/GSPredicate2.swift $out/GSPredicate.swift

                 perl -i -0pe 's|(?<=\W)UniChar(?=\W)|UInt16|g' $out/GSPredicate.swift
                 perl -i -0pe 's|(_subs\[[^\]]+\])!|$1|g' $out/GSPredicate.swift
                 perl -i -0pe 's|func\s+GSICUStringMatchesRegex.+?\)\s+->\s+\w+\s+\{\K[\S\s]+?return result|return false|' $out/GSPredicate.swift
          '';
       }; in
       let predicateHeaderKt = stdenv.mkDerivation rec {
                 inherit predicateConcatenated;
                 name = "GSPredicateHeader.kt";
                 src = ./.;
                 dontBuild = true;

                 installPhase = ''
                 set -ex
                 mkdir -p $out;

                 grep -Pzo '\/\*([^*]*\*(?!\/))*[^*]*\([Cc]\)([^*]*\*(?!\/))*[^*]*\*\/\s*\n' $predicateConcatenated/* > $out/GSPredicateHeader.kt
                 sed -i 's/\x0//g' $out/GSPredicateHeader.kt
                 patch --merge --ignore-whitespace --verbose -o $out/GSPredicateHeader2.kt -p2 $out/GSPredicateHeader.kt GSPredicateHeader.kt.patch
                 rm $out/GSPredicateHeader.kt
                 mv $out/GSPredicateHeader2.kt $out/GSPredicateHeader.kt
                 '';
       }; in
       let predicateKt = stdenv.mkDerivation rec {
                 inherit predicateSwift;
                 inherit predicateHeaderKt;

                 name = "GSPredicate.kt";
                 src = [ ./. ];
                 buildInputs = [ pkgs.perl gryphon ];
                 dontBuild = true;
                 installPhase = ''
                 set -ex
                 mkdir -p $out;

                 install -Dm644 $predicateHeaderKt/* $out/GSPredicate.kt

                 export HOME=$PWD
                 gryphon clean init -xcode
                 gryphon --write-to-console --no-main-file $predicateSwift/* >> $out/GSPredicate.kt || (cat $out/GSPredicate.kt; false)

                 perl -i -0pe 's@\?\:\s*\"\"@\?\: \"\"\.toNSString()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@\.toString\(\)@\.toNSString()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(class.+\: NSObject)@$1\(\)@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(class.+\: NSPredicate)@$1\(\)@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(class.+\: NSExpression)(?!\()@$1\(\)@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(class.+)\: NSCompoundPredicate(?=\W)(?:\(\))?@$1\(type: NSCompoundPredicateType, list: List<NSPredicate>): NSCompoundPredicate\(type, list\)@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(?<=\W)Array\(([^)]+(?:\([^)]*\))?)\)(?!\))@$1\.toMutableList()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(var\s+\w+)\s*:\s*(?:List|Array)(<\w+>)(\s*=\s*)(?:mutable)?(?:[Ll]ist|[Aa]rray)(?:Of)?(?:\2)?@$1$3mutableListOf$2@g' $out/GSPredicate.kt
                 perl -i -0pe 's@IndexingIterator\<[^>]+\<([^>]+)\>\>@IndexingIterator\<$1\>@g' $out/GSPredicate.kt
                 perl -i -0pe 's@required constructor@constructor@g' $out/GSPredicate.kt
                 perl -i -0pe 's@\= \"[^"]*\"$@$0\.toNSString()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@return \"[^"]*\"$@$0\.toNSString()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@"([,\)\$])(?!")@".toNSString()$1@g' $out/GSPredicate.kt
                 perl -i -0pe 's@[\t ]+(?:var|let) unused[\w:=".\(\) ]+\r?\n?@@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(?<=\W)unused(?=\W)@InOut()@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(?<=\W)String\.UTF16View\.Index(?=\W)@StringUTF16ViewIndex@g' $out/GSPredicate.kt
                 perl -i -0pe 's@(?<=\W)String(?=\W)@NSString@g' $out/GSPredicate.kt
                 perl -i -0pe 's@\s*\.\.<\s*@ until @g' $out/GSPredicate.kt

                 patch --merge --ignore-whitespace --verbose -o $out/GSPredicate2.kt -p2 $out/GSPredicate.kt GSPredicate.kt.patch
                 rm $out/GSPredicate.kt
                 mv $out/GSPredicate2.kt $out/GSPredicate.kt
                 '';
       }; in
       let wdaConcatenated = stdenv.mkDerivation {
                 name = "WebDriverAgent.m";
                 src = ./.;
                 dontBuild = true;
                 installPhase = ''
                 set -ex
                 mkdir -p $out

                 cat ${gnuStepBaseSrc}/Headers/GNUstepBase/GSVersionMacros.h ${gnuStepBaseSrc}/Headers/GNUstepBase/GNUstep.h \
                 ${gnuStepBaseSrc}/Headers/Foundation/NSObjCRuntime.h ${gnuStepBaseSrc}/Headers/Foundation/NSException.h
                 >> $out/WebDriverAgent.m
                 cat \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBPredicate.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBPredicate.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/NSPredicate+FBFormat.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/NSPredicate+FBFormat.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/NSExpression+FBFormat.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/NSExpression+FBFormat.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBConfiguration.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBConfiguration.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Routing/FBElementUtils.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Routing/FBElementUtils.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Routing/FBElement.h \
                 ${webDriverAgentSrc}/PrivateHeaders/XCTest/XCAccessibilityElement.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBClassChainQueryParser.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBClassChainQueryParser.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/XCUIElement+FBClassChain.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/XCUIElement+FBClassChain.m \
                 ${webDriverAgentSrc}/PrivateHeaders/XCTest/XCUIElementQuery.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBXCodeCompatibility.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Utilities/FBXCodeCompatibility.m \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/XCUIElement+FBFind.h \
                 ${webDriverAgentSrc}/WebDriverAgentLib/Categories/XCUIElement+FBFind.m \
                 >> $out/WebDriverAgent.m
                 '';
       }; in
       let wdaSwift = stdenv.mkDerivation {
                 inherit wdaConcatenated;

                 name = "WebDriverAgent.swift";
                 src = ./.;
                 dontBuild = true;
                 buildInputs = [ pkgs.perl objc2swift ];
                 installPhase = ''
                 set -ex
                 mkdir -p $out

                 echo > $out/WebDriverAgent.m
                 echo '#define OS_API_VERSION(v, m) 1' >> $out/WebDriverAgent.m
                 echo '#define GS_USE_ICU 1' >> $out/WebDriverAgent.m
                 echo '#define BASE_NATIVE_OBJC_EXCEPTIONS 1' >> $out/WebDriverAgent.m
                 echo '#define OBJC_ZEROCOST_EXCEPTIONS 1' >> $out/WebDriverAgent.m
                 echo '#define NS_FORMAT_FUNCTION(a, b)' >> $out/WebDriverAgent.m
                 echo '#define DEFINE_BLOCK_TYPE(a, b, c, d)' >> $out/WebDriverAgent.m
                 echo '#define YES YES' >> $out/WebDriverAgent.m
                 echo '#define NO NO' >> $out/WebDriverAgent.m
                 echo '#define nil nil' >> $out/WebDriverAgent.m
                 echo '#define UINT_MAX 0xffffffff' >> $out/WebDriverAgent.m
                 echo '#define __LONG_LONG_MAX__ 9223372036854775807ULL' >> $out/WebDriverAgent.m

                 cat $wdaConcatenated/* >> $out/WebDriverAgent.m

                 mkdir home
                 HOME=$PWD/home objc2swift --init macos
                 HOME=$PWD/home objc2swift --skip-header $out/WebDriverAgent.m -o $out/WebDriverAgent.swift
                 rm $out/WebDriverAgent.m

                 perl -i -0pe 's#let(\s+\w+\s*:\s*)NSMutable(Array|Set)!(\s*=\s*)?$#var$1$2<AnyObject>$3#g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|let(\s+\w+\s*:\s*)NSMutable(Dictionary)!(\s*=\s*)?$|var$1$2<String, AnyObject>$3|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.valueForKey\(([^)]+)\)|[$1]|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|([\w.]+)\.substringToIndex\(([^)]+)\)|String($1\.characters\.prefix\($2))|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|([\w.]+)\.substringFromIndex\(([^)]+)\)|String($1\.characters\.suffix\($2))|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|([\w.]+)\.substringWithRange\(\s*NSMakeRange\(([^,)]+),\s*([^,)]+)\)\s*\)|String($1\.characters[$2 ..< $2 + $3])|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|:\s*UInt\s*= (\w+)\.length| = $1.length|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\=)\s+([^.]+)\.characterAtIndex\(([^)]+)\)(?!\))|$1.utf16\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\=)\s+(.+?)\.characterAtIndex\(([^)]+\(\))\)(?!\))|$1.utf16\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\=)\s+(.+?)\.characterAtIndex\(([^)]+\(\))\)(?!\))|$1.utf16\[$1.utf16.index($1.utf16.startIndex, offsetBy: $2)\]|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\W)unichar(?=\W)|UInt16|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(\s*\:\s*)(Array\<AnyObject\>)(\s*\=\s*)NSMutableArray\.array\(\)|$1$2$3$2\(\)|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(\s*:\s*[A-Z]\w+)\!|$1|g' $out/WebDriverAgent.swift # TODO: improve this
                 perl -i -0pe 's|\@throw|throw|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|NSException\.exceptionWithName\(\s*(\w+)\s*,\s*reason:\s*([^,)]+?)\s*,\s*userInfo:[^)]+\)|NSError\(domain: "$1", code: 0, userInfo: \[NSLocalizedDescriptionKey: $2\]\)|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|class\s+func\s+(\w+)\(\s*\)\s*->\s*([^{]+)\s*(\{[^{}]+)dispatch_once\([^,)]+,\s+(\{[\S\s]+?\})\s*\)([^}+]+?\})|lazy class var $1: $2 = $3$4$5\(\)|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.isKindOfClass\(NSNull\.self\)| == nil|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(for\s+\w+\s*):\s*[A-Z]\w+|$1|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.length(?=\W)|\.count|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.lowercaseString(?:\(\))?(?=\W)|\.lowercased()|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.uppercaseString(?:\(\))?(?=\W)|\.uppercased()|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\W)NSCharacterSet!(?=\W)|NSCharacterSet|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's#(\-\> (?:\[AnyObject\]|NSString|NSError))\!#$1#g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|;\s*;|;|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|followingTokens\(\) \-\> \[AnyObject\]|followingTokens\(\) \-\> \[AnyClass\]|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|self\.self\.|self\.|g' $out/WebDriverAgent.swift
                 perl -i -0pe  's|\:UInt\s*=\s*(.+)\.count\(\)| = $1.count|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.isKindOfClass\(([^)]+)\.self\)| is $1|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\.firstObject(?=\W)|\.first!|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(func\s+\w+\s*\(\s*)(?=\w)(?=[^_])|$1_ |g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<!case )(?<!\.)(?<!")(XCUIElementType\w)|XCUIElementType\.$1|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|(?<=\W)integerValue(?=\W)|intValue|g' $out/WebDriverAgent.swift
                 perl -i -0pe 's|\$\(IncludeDirective\)\r?\n||g' $out/WebDriverAgent.swift

                 patch --merge --ignore-whitespace --verbose -o $out/WebDriverAgent2.swift -p2 $out/WebDriverAgent.swift WebDriverAgent.swift.patch
                 rm $out/WebDriverAgent.swift
                 mv $out/WebDriverAgent2.swift $out/WebDriverAgent.swift

                        '';
                     };
       in
       let wdaKt = stdenv.mkDerivation rec {
                 inherit predicateSwift;
                 inherit wdaSwift;

                 name = "WebDriverAgent.kt";
                 src = [ ./. ];
                 buildInputs = [ pkgs.perl pkgs.pcre2 gryphon ];
                 dontBuild = true;
                 installPhase = ''
                 set -ex
                 mkdir -p $out;

                 echo 'import Foundation' >> $out/WebDriverAgent.swift
                 cat $wdaSwift/* >> $out/WebDriverAgent.swift

                 export HOME=$PWD
                 gryphon clean init -xcode
                 gryphon --write-to-console --no-main-file $out/WebDriverAgent.swift --skip $predicateSwift/* > $out/WebDriverAgent.kt || (cat $out/WebDriverAgent.kt; false)
                 rm $out/WebDriverAgent.swift

                 perl -i -0pe 's@String\.UTF16View\.Index@StringUTF16ViewIndex@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@\.\.<@until@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@String\.CompareOptions\.@StringCompareOption\.@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(fun\s+(\w+)\.[\w.]+\s*\([^)]+(?:\([^)]+\))?[^)]+\)\s*:\s*)Self@$1$2@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(var\s+\w+)\s*:\s*(?:List|Array)(<\w+>)(\s*=\s*)(?:mutable)?(?:[Ll]ist|[Aa]rray)(?:Of)?(?:\2)?@$1$3mutableListOf$2@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(var\s+\w+)\s*:\s*Set(<\w+>)(\s*=\s*)(?:mutable)?Set(?:Of)?(?:\2)?@$1$3mutableSetOf$2@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(var\s+\w+)\s*:\s*Map(<[\w, ]+>)(\s*=\s*)(?:mutable)?(?:[Mm]ap)(?:Of)?(?:\2)?@$1$3mutableMapOf$2@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(?<=\W)Array\(([^)]+)\)@$1\.toMutableList()@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@"\.firstOrNull\(\)\!\!@"\.single()@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(class.+\: NSObject)@$1\(\)@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(class.+)\: FBBaseClassChainToken(?:\(\))?@$1\(stringValue: String): FBBaseClassChainToken\(stringValue\)@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(class.+)\: FBAbstractPredicateToken(?:\(\))?@$1\(stringValue: String): FBAbstractPredicateToken\(stringValue\)@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@\.this@::class@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@\.dynamicType@@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@Token::class@Token\(NSString\.emptyString\)@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@\:\s*List\<AnyClass\>@\: List\<FBBaseClassChainToken\>@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(?<=\W)String\.UTF16View\.Index(?=\W)@StringUTF16ViewIndex@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@(?<=\W)String(?=\W)@NSString@g' $out/WebDriverAgent.kt
                 perl -i -0pe 's@\s*\.\.<\s*@ until @g' $out/WebDriverAgent.kt

                 patch --merge --ignore-whitespace --verbose --fuzz 10 -o $out/WebDriverAgent2.kt -p2 $out/WebDriverAgent.kt WebDriverAgent.kt.patch
                 rm $out/WebDriverAgent.kt
                 mv $out/WebDriverAgent2.kt $out/WebDriverAgent.kt
                 '';
       }; in
       stdenv.mkDerivation rec {
                        inherit predicateKt;
                        inherit wdaKt;

                        name = "output";
                        src = [ ./. ];
                        dontBuild = true;
                        installPhase = ''
                        set -ex
                        mkdir -p $out;
                        cp $predicateKt/*.kt $out
                        cp $wdaKt/*.kt $out
                        '';
       };
  };
}
