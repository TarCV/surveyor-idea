/*
 *  Copyright (C) 2024 TarCV
 *
 *  This file is part of UI Surveyor.
 *  UI Surveyor is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  This file is based on the parts of WebDriverAgent which are
 *  (copies of referenced files can be found in ipredicate/licenses/wda subdirectory
 *  in this repository):
 *
 *      Copyright (c) 2015-present, Facebook, Inc.
 *      All rights reserved.
 *
 *      This source code is licensed under the BSD-style license found in the
 *      LICENSE file in the root directory of this source tree. An additional grant
 *      of patent rights can be found in the PATENTS file in the same directory.
 */
package com.github.tarcv.testingteam.surveyor.ipredicate
import com.github.tarcv.testingteam.surveyor.ipredicate.NSString.Companion.toNSString
import com.github.tarcv.testingteam.surveyor.ipredicate.XCUIElementType.Companion.XCUI_ELEMENT_TYPE_APPLICATION
import java.lang.Math.abs
import kotlin.reflect.KProperty

internal interface XCElementSnapshot

internal open class FBElementTypeTransformer {
    companion object {
        fun elementTypeWithTypeName(typeName: NSString): XCUIElementType {
            @Suppress("DEPRECATION")
            return XCUIElementType.fromTypeString(typeName.toString())
        }
    }
}

annotation class TARGET_OS_TV

// define OS_API_VERSION(v, m) 1
// define GS_USE_ICU 1
// define BASE_NATIVE_OBJC_EXCEPTIONS 1
// define OBJC_ZEROCOST_EXCEPTIONS 1
// define NS_FORMAT_FUNCTION(a, b)
// define DEFINE_BLOCK_TYPE(a, b, c, d)
// define YES YES
// define NO NO
// define nil nil
// define UINT_MAX 0xffffffff
// define __LONG_LONG_MAX__ 9223372036854775807ULL
//#import <Foundation/Foundation.h>
//#import "FBPredicate.h"
object FBPredicate {
        fun predicateWithFormat(predicateFormat: NSString, args: List<AnyObject>): NSPredicate {
            val predicate: NSPredicate = NSPredicate.predicateWithFormat(predicateFormat, args)
            val hackPredicate: NSPredicate = NSPredicate.predicateWithFormat(this.forceResolvePredicateString())
            return NSCompoundPredicate.andPredicateWithSubpredicates(listOf(predicate, hackPredicate))
        }

        fun forceResolvePredicateString(): NSString {
            return "1 == 1 or identifier == 0 or frame == 0 or value == 0 or title == 0 or label == 0 or elementType == 0 or enabled == 0 or placeholderValue == 0 or selected == 0"
                .toNSString()
        }
}


//#import <Foundation/Foundation.h>
//#import "NSPredicate+FBFormat.h"
//#import "FBPredicate.h"
//#import "NSExpression+FBFormat.h"
internal fun NSPredicate.Companion.fb_predicateWithPredicate(
    original: NSPredicate,
    comparisonModifier: (NSComparisonPredicate) -> NSPredicate)
    : NSPredicate
{
    if (original is NSCompoundPredicate) {
        val compPred: NSCompoundPredicate = original as NSCompoundPredicate
        var predicates = mutableListOf<NSPredicate>()

        for (predicate in compPred.subpredicates()) {
            if ((predicate.predicateFormat().lowercased() == FBPredicate.forceResolvePredicateString().lowercased())) {
                // Do not translete this predicate
                predicates.addObject(predicate)
                continue
            }
            val newPredicate: NSPredicate? = NSPredicate.fb_predicateWithPredicate(predicate, comparisonModifier)
            if (null != newPredicate) {
                predicates.addObject(newPredicate)
            }
        }

        return NSCompoundPredicate(compPred.compoundPredicateType(), predicates)
    }
    if (original is NSComparisonPredicate) {
        return comparisonModifier((original as NSComparisonPredicate))
    }
    return original
}

internal fun NSPredicate.Companion.fb_formatSearchPredicate(input: NSPredicate): NSPredicate {
    return NSPredicate.fb_predicateWithPredicate(
        input,
        { cp ->
                    val left: NSExpression = NSExpression.fb_wdExpressionWithExpression(cp.leftExpression())
                    val right: NSExpression = NSExpression.fb_wdExpressionWithExpression(cp.rightExpression())

                    return@fb_predicateWithPredicate NSComparisonPredicate.predicateWithLeftExpression(
                        left,
                        right,
                        cp.comparisonPredicateModifier(),
                        cp.predicateOperatorType(),
                        cp.options())
                })
}

//#import <Foundation/Foundation.h>
//#import "NSExpression+FBFormat.h"
//#import "FBElementUtils.h"
internal fun NSExpression.Companion.fb_wdExpressionWithExpression(input: NSExpression): NSExpression {
    if (!(input is GSKeyPathExpression)) {
        return input
    }

    val propName: NSString = (input as GSKeyPathExpression).keyPath()
    val dotPos: Int? = propName.indexOrNull(".".single())

    if (null != dotPos) {
        val actualPropName:NSString = NSString(propName.characters.prefix(dotPos))
        val suffix: NSString = propName.suffix(dotPos + 1)
        return NSExpression.expressionForKeyPath(
            NSString(
                    "%@.%@",
                    FBElementUtils.wdAttributeNameForAttributeName(actualPropName),
                    NSString(propName.prefix(propName.index(propName.startIndex, dotPos)))))
    }

    return NSExpression.expressionForKeyPath(FBElementUtils.wdAttributeNameForAttributeName(propName))
}

object FBConfiguration: NSObject() {
        fun setUseFirstMatch(enabled: Boolean) {
            FBShouldUseFirstMatch = enabled
        }

        fun useFirstMatch(): Boolean {
            return FBShouldUseFirstMatch
        }

        fun setBoundElementsByIndex(enabled: Boolean) {
            FBShouldBoundElementsByIndex = enabled
        }

        fun boundElementsByIndex(): Boolean {
            return FBShouldBoundElementsByIndex
        }

        fun setIncludeNonModalElements(isEnabled: Boolean) {
            FBIncludeNonModalElements = isEnabled
        }

        fun includeNonModalElements(): Boolean {
            return FBIncludeNonModalElements
        }

    var tvMode: Boolean = false
    private var FBShouldUseFirstMatch: Boolean = false
    private var FBShouldBoundElementsByIndex: Boolean = false
    private var FBIncludeNonModalElements: Boolean = false
}

//#import <objc/runtime.h>
//#import "FBElementUtils.h"
//#import "FBElementTypeTransformer.h"
internal val FBUnknownAttributeException: String = "FBUnknownAttributeException"
internal val WD_PREFIX: NSString = "wd".toNSString()

internal object FBElementUtils: NSObject() {
        fun wdAttributeNameForAttributeName(name: NSString): NSString {
            require(name.count > 0, { "Attribute name cannot be empty" })

            val attributeNamesMapping: Map<NSString, NSString> = FBElementUtils.wdAttributeNamesMapping
            val result: NSString? = attributeNamesMapping[name]

            if (null == result) {
                val description: String = NSString(
                    "The attribute '%@' is unknown. Valid attribute names are: %@",
                    name,
                    attributeNamesMapping.keys.toString()
                ).toString()
                throw NSError("FBUnknownAttributeException", 0, mapOf(NSLocalizedDescriptionKey to description))
            }

            return result
        }

        fun uniqueElementTypesWithElements(elements: List<FBElement>): Set<XCUIElementType> {
            var matchingTypes = mutableSetOf<XCUIElementType>()
            for (element in elements) {
                matchingTypes.addObject(FBElementTypeTransformer.elementTypeWithTypeName(element.wdType))
            }
            return Set(matchingTypes)
        }

    val wdAttributeNamesMapping: Map<NSString, NSString> by lazy {
            var attributeNamesMapping: Map<NSString, NSString>

                    var wdPropertyGettersMapping = mutableMapOf<NSString, NSString?>()
                    val propsCount = InOut<UInt>()
                    val aProtocol = FBElement::class
                    val properties: List<KProperty<*>> = aProtocol.members.filterIsInstance<KProperty<*>>()
                    var i: Int = 0

                    for (property in properties) {
                        val nsName: String = property.name

                        if (null == nsName || !nsName.startsWith(WD_PREFIX.toString())) {
                            continue
                        }

                        // ??? wdPropertyGettersMapping.setObject(NSNull.null(), nsName)

                        val include = FBConfiguration.tvMode || property.annotations.none { it is TARGET_OS_TV }
                        if (property.getter != null && include) {
                            wdPropertyGettersMapping.setObject(nsName.toNSString(), nsName.toNSString())
                        }

                    }

                    var resultCache = mutableMapOf<NSString, NSString>()

                    for ((propName, unused259) in wdPropertyGettersMapping) {
                        wdPropertyGettersMapping[propName].let { propNameValue ->
                            if (propNameValue == null) {
                                // no getter
                                resultCache.setValue(propName, propName)
                            } else {
                                // has getter method
                                resultCache.setValue(propNameValue, propName)
                            }
                        }

                        var aliasName: NSString

                        if (propName.length <= WD_PREFIX.length + 1) {
                            aliasName = NSString("%@", propName.characters.substring(WD_PREFIX.length, (WD_PREFIX.length + 1)).lowercased())
                        }
                        else {
                            val propNameWithoutPrefix: NSString = propName.characters.suffix(WD_PREFIX.length)
                            var firstPropNameCharacter: NSString = propNameWithoutPrefix.characters.substring(0, 0 + 1)

                            if (!(propNameWithoutPrefix == propNameWithoutPrefix.uppercased())) {
                                // Lowercase the first character for the alias if the property name is not an uppercase abbreviation
                                firstPropNameCharacter = firstPropNameCharacter.lowercased()
                            }

                            aliasName = NSString("%@%@", firstPropNameCharacter, propNameWithoutPrefix.characters.suffix(1))
                        }

                        wdPropertyGettersMapping[propName].let { propNameValue ->
                            if (propNameValue == null) {
                                // no getter
                                resultCache.setValue(propName, aliasName)
                            } else {
                                // has getter method
                                resultCache.setValue(propNameValue, aliasName)
                            }
                        }
                    }

                    attributeNamesMapping = resultCache.toMap()

            attributeNamesMapping.toMap()
        }
}

//#import <CoreGraphics/CoreGraphics.h>
//#import <Foundation/Foundation.h>
interface FBElement {
    //! Element's frame in normalized (rounded dimensions without Infinity values) CGRect format 
    val wdFrame: CGRect

    //! Element's wsFrame in NSDictionary format 
    val wdRect: NSDictionary

    //! Element's name 
    val wdName: NSString?

    //! Element's label 
    val wdLabel: NSString?

    //! Element's selected state 
    val wdSelected: Boolean

    //! Element's type 
    val wdType: NSString

    //! Element's value 
    val wdValue: NSString?

    //! Element's unique identifier 
    val wdUID: NSString?

    //! Whether element is enabled 
    val wdEnabled: Boolean

    //! Whether element is visible 
    val wdVisible: Boolean

    //! Whether element is accessible 
    val wdAccessible: Boolean

    //! Whether element is an accessibility container (contains children of any depth that are accessible) 
    val wdAccessibilityContainer: Boolean

    //! Whether element is focused 
    @TARGET_OS_TV val wdFocused: Boolean

    //! Element's index relatively to its parent. Starts from zero 
    val wdIndex: Int

}

//
//     Generated by class-dump 3.5 (64 bit).
//
//     class-dump is Copyright (C) 1997-1998, 2000-2001, 2004-2013 by Steve Nygard.
//
//#import <XCTest/XCTest.h>
//#import "FBClassChainQueryParser.h"
//#import "FBErrorBuilder.h"
//#import "FBElementTypeTransformer.h"
//#import "FBExceptions.h"
//#import "FBPredicate.h"
//#import "NSPredicate+FBFormat.h"
internal open class FBBaseClassChainToken(stringValue: NSString): NSObject() {
    private var _asString: NSString = stringValue
    open var asString: NSString
        get() {
            return _asString
        }
        set(newValue) {
            _asString = newValue
        }
    private var _previousItemsCountToOverride: Int = 0
    open var previousItemsCountToOverride: Int
        get() {
            return _previousItemsCountToOverride
        }
        set(newValue) {
            _previousItemsCountToOverride = newValue
        }

    open fun allowedCharacters(): NSCharacterSet {
        // This method is expected to be overriden by subclasses
        return NSCharacterSet.characterSetWithCharactersInString("")
    }

    open fun maxLength(): Int {
        // This method is expected to be overriden by subclasses
        return Int.MAX_VALUE
    }

    open fun followingTokens(): List<FBBaseClassChainToken> {
        // This method is expected to be overriden by subclasses
        return listOf()
    }

    open fun canConsumeCharacter(character: UShort): Boolean {
        return this.allowedCharacters().characterIsMember(character)
    }

    open fun appendChar(character: UShort) {
        var value: NSString = this.asString
        value = value.appendFormat("%C", character)
        this.asString = value
    }

    open fun followingTokenBasedOn(character: UShort): FBBaseClassChainToken? {
        for (matchingTokenClass in this.followingTokens()) {
            if (matchingTokenClass.canConsumeCharacter(character)) {
                return matchingTokenClass.nextTokenWithCharacter(character)
            }
        }
        return null
    }

    open fun nextTokenWithCharacter(character: UShort): FBBaseClassChainToken? {
        if (this.canConsumeCharacter(character = character) && this.asString.length < this.maxLength()) {
            this.appendChar(character = character)
            return this
        }
        return this.followingTokenBasedOn(character = character)
    }
}

internal open class FBClassNameToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.letterCharacterSet
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBSplitterToken(NSString.emptyString), FBOpeningBracketToken(NSString.emptyString))
    }
}

internal val STAR_TOKEN: NSString = "*".toNSString()

internal open class FBStarToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.characterSetWithCharactersInString(STAR_TOKEN)
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBSplitterToken(NSString.emptyString), FBOpeningBracketToken(NSString.emptyString))
    }

    override open fun nextTokenWithCharacter(character: UShort): FBBaseClassChainToken? {
        if (this.allowedCharacters().characterIsMember(character)) {
            if (this.asString.length >= 1) {
                val nextToken: FBDescendantMarkerToken = FBDescendantMarkerToken(NSString("%@%@", STAR_TOKEN, STAR_TOKEN))
                nextToken.previousItemsCountToOverride = 1
                return nextToken
            }
            this.appendChar(character = character)
            return this
        }
        return this.followingTokenBasedOn(character = character)
    }
}

internal val DESCENDANT_MARKER: NSString = "**/".toNSString()

internal open class FBDescendantMarkerToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.characterSetWithCharactersInString("*/")
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBClassNameToken(NSString.emptyString), FBStarToken(NSString.emptyString))
    }

    override open fun maxLength(): Int {
        return 3
    }

    override open fun nextTokenWithCharacter(character: UShort): FBBaseClassChainToken? {
        if (this.allowedCharacters().characterIsMember(character) && this.asString.count <= this.maxLength()) {
            if (this.asString.length > 0 && !DESCENDANT_MARKER.startsWith(this.asString)) {
                return null
            }
            if (this.asString.count < this.maxLength()) {
                this.appendChar(character = character)
                return this
            }
        }
        return this.followingTokenBasedOn(character = character)
    }
}

internal open class FBSplitterToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.characterSetWithCharactersInString("/")
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBStarToken(NSString.emptyString), FBClassNameToken(NSString.emptyString))
    }

    override open fun maxLength(): Int {
        return 1
    }
}

internal open class FBOpeningBracketToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.characterSetWithCharactersInString("[")
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBNumberToken(NSString.emptyString), FBSelfPredicateToken(NSString.emptyString), FBDescendantPredicateToken(NSString.emptyString))
    }

    override open fun maxLength(): Int {
        return 1
    }
}

internal open class FBNumberToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        val result: NSMutableCharacterSet = NSMutableCharacterSet()

        result.formUnionWithCharacterSet(NSCharacterSet.decimalDigitCharacterSet)
        result.addCharactersInString("-")

        return result
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBClosingBracketToken(NSString.emptyString))
    }
}

internal open class FBClosingBracketToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.characterSetWithCharactersInString("]")
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBSplitterToken(NSString.emptyString), FBOpeningBracketToken(NSString.emptyString))
    }

    override open fun maxLength(): Int {
        return 1
    }
}

internal abstract class FBAbstractPredicateToken(stringValue: NSString): FBBaseClassChainToken(stringValue) {
    abstract val enclosingMarker: NSString

    private var _isParsingCompleted: Boolean = false
    open var isParsingCompleted: Boolean
        get() {
            return _isParsingCompleted
        }
        set(newValue) {
            _isParsingCompleted = newValue
        }

    override open fun allowedCharacters(): NSCharacterSet {
        return NSCharacterSet.illegalCharacterSet.invertedSet
    }

    override open fun followingTokens(): List<FBBaseClassChainToken> {
        return listOf(FBClosingBracketToken(NSString.emptyString))
    }

    override open fun canConsumeCharacter(character: UShort): Boolean {
        return NSCharacterSet.characterSetWithCharactersInString(this.enclosingMarker).characterIsMember(
            character)
    }

    open fun stripLastChar() {
        if (this.asString.length > 0) {
            this.asString = this.asString.substringToIndex(this.asString.count - 1)
        }
    }

    override open fun nextTokenWithCharacter(character: UShort): FBBaseClassChainToken? {
        val currentChar: NSString = NSString("%C", character)

        if (!this.isParsingCompleted && this.allowedCharacters().characterIsMember(character)) {
            if (0 == this.asString.length) {
                if ((this.enclosingMarker == currentChar)) {
                    // Do not include enclosing character
                    return this
                }
            }
            else if ((this.enclosingMarker == currentChar)) {
                this.appendChar(character = character)
                this.isParsingCompleted = true
                return this
            }
            this.appendChar(character = character)
            return this
        }

        if (this.isParsingCompleted) {
            if ((currentChar == this.enclosingMarker)) {
                // Escaped enclosing character has been detected. Do not finish parsing
                this.isParsingCompleted = false
                return this
            }
            else {
                // Do not include enclosing character
                this.stripLastChar()
            }
        }

        return this.followingTokenBasedOn(character = character)
    }
}

internal open class FBSelfPredicateToken(stringValue: NSString): FBAbstractPredicateToken(stringValue) {
        override val enclosingMarker: NSString get() {
            return "`".toNSString()
        }
}

internal open class FBDescendantPredicateToken(stringValue: NSString): FBAbstractPredicateToken(stringValue) {
        override val enclosingMarker: NSString get() {
            return "\$".toNSString()
        }
}

internal open class FBClassChainItem(
    type: XCUIElementType,
    position: Int,
    predicates: List<AnyObject>,
    isDescendant: Boolean): NSObject() {
    private var _position: Int = 0
    open val position: Int
        get() {
            return _position
        }
    private var _type: XCUIElementType
    open val type: XCUIElementType
        get() {
            return _type
        }
    private var _isDescendant: Boolean
    open val isDescendant: Boolean
        get() {
            return _isDescendant
        }
    private var _predicates: List<AnyObject>
    open val predicates: List<AnyObject>
        get() {
            return _predicates
        }

    init
    {
            _type = type
            _position = position
            _predicates = predicates
            _isDescendant = isDescendant
    }
}

internal open class FBClassChain(elements: List<FBClassChainItem>): NSObject() {
    private var _elements: List<FBClassChainItem>
    open val elements: List<FBClassChainItem>
        get() {
            return _elements
        }

    init {
            _elements = elements
    }
}

internal open class FBClassChainQueryParser: NSObject() {
    companion object {
        fun tokenizationErrorWithIndex(index: Int, originalQuery: NSString): NSError {
            val description: NSString = NSString(
                "Cannot parse class chain query '%@'. Unexpected character detected at position %@:\n%@ <----",
                originalQuery,
                index + 1u,
                originalQuery.substring(0, originalQuery.index(0, index + 1u)))
            return FBErrorBuilder.builder.withDescription(description).build()
        }

        fun tokenizedQueryWithQuery(classChainQuery: NSString, error: InOut<NSError>): List<FBBaseClassChainToken>? {
            val queryStringLength: Int = classChainQuery.length
            var token: FBBaseClassChainToken
            val firstCharacter: UShort = classChainQuery.utf16[classChainQuery.utf16.index(classChainQuery.utf16.startIndex, 0)]

            if (classChainQuery.startsWith(DESCENDANT_MARKER)) {
                token = FBDescendantMarkerToken(DESCENDANT_MARKER)
            }
            else if (FBClassNameToken(NSString.emptyString).canConsumeCharacter(firstCharacter)) {
                token = FBClassNameToken(NSString("%C", firstCharacter))
            }
            else if (FBStarToken(NSString.emptyString).canConsumeCharacter(firstCharacter)) {
                token = FBStarToken(NSString("%C", firstCharacter))
            }
            else {
                if ((error != null)) {
                    error `=` this.tokenizationErrorWithIndex(0, classChainQuery)
                }
                return null
            }

            var result = mutableListOf<FBBaseClassChainToken>()
            var nextToken: FBBaseClassChainToken? = token
            var charIdx: Int = token.asString.length

            while (charIdx < queryStringLength) {
                nextToken = token.nextTokenWithCharacter(classChainQuery.characterAtIndex(charIdx))

                if (null == nextToken) {
                    if ((error != null)) {
                        error `=` this.tokenizationErrorWithIndex(charIdx, classChainQuery)
                    }
                    return null
                }

                if (nextToken != token) {
                    result.addObject(token)
                    if (nextToken.previousItemsCountToOverride > 0 && result.size > 0) {
                        val itemsCountToOverride: Int = if (nextToken.previousItemsCountToOverride <= result.size) { nextToken.previousItemsCountToOverride } else { result.size }
                        result.removeObjectsInRange(NSMakeRange(result.count - itemsCountToOverride, itemsCountToOverride))
                    }
                    token = nextToken
                }

                charIdx += 1
            }

            if ((nextToken != null)) {
                if (nextToken.previousItemsCountToOverride > 0 && result.size > 0) {
                    val itemsCountToOverride: Int = if (nextToken.previousItemsCountToOverride <= result.size) { nextToken.previousItemsCountToOverride } else { result.size }
                    result.removeObjectsInRange(NSMakeRange(result.count - itemsCountToOverride, itemsCountToOverride))
                }
                result.addObject(nextToken)
            }

            val lastToken: FBBaseClassChainToken? = result.lastOrNull()

            if (!(lastToken is FBClosingBracketToken || lastToken is FBClassNameToken || lastToken is FBStarToken)) {
                if ((error != null)) {
                    error `=` this.tokenizationErrorWithIndex(queryStringLength - 1, classChainQuery)
                }
                return null
            }

            return result.toList()
        }

        fun compilationErrorWithQuery(originalQuery: NSString, description: NSString): NSError {
            val fullDescription: NSString = NSString("Cannot parse class chain query '%@'. %@", originalQuery, description)
            return FBErrorBuilder.builder.withDescription(fullDescription).build()
        }

        fun compiledQueryWithTokenizedQuery(
            tokenizedQuery: List<FBBaseClassChainToken>,
            originalQuery: NSString,
            error: InOut<NSError>)
            : FBClassChain?
        {
            var result = mutableListOf<FBClassChainItem>()
            var chainElementType: XCUIElementType = XCUIElementType.XCUI_ELEMENT_TYPE_ANY
            var chainElementPosition: Int = 0
            var isTypeSet: Boolean = false
            var isPositionSet: Boolean = false
            var isDescendantSet: Boolean = false
            var predicates = mutableListOf<AnyObject>()

            for (token in tokenizedQuery) {
                if (token is FBClassNameToken) {
                    if (isTypeSet) {
                        val description: NSString = NSString("Unexpected token '%@'. The type name can be set only once.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }
                    try {
                        chainElementType = FBElementTypeTransformer.elementTypeWithTypeName(typeName = token.asString)
                        isTypeSet = true
                    }
                    catch (e: Exception) {
                        if (e is IllegalArgumentException == true) {
                            val description: NSString = NSString("'%@' class name is unknown to WDA", token.asString)
                            error `=` this.compilationErrorWithQuery(originalQuery, description)
                            return null
                        }
                        throw e
                    }
                }
                else if (token is FBStarToken) {
                    if (isTypeSet) {
                        val description: NSString = NSString("Unexpected token '%@'. The type name can be set only once.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }
                    chainElementType = XCUIElementType.XCUI_ELEMENT_TYPE_ANY
                    isTypeSet = true
                }
                else if (token is FBDescendantMarkerToken) {
                    if (isDescendantSet) {
                        val description: NSString = NSString("Unexpected token '%@'. Descendant markers cannot be duplicated.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }

                    isTypeSet = false
                    isPositionSet = false

                    predicates.removeAllObjects()

                    isDescendantSet = true
                }
                else if (token is FBAbstractPredicateToken) {
                    if (isPositionSet) {
                        val description: NSString = NSString("Predicate value '%@' must be set before position value.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }

                    if (!(token as FBAbstractPredicateToken).isParsingCompleted) {
                        val description: NSString = NSString("Cannot find the end of '%@' predicate value.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }

                    val value: NSPredicate = NSPredicate.fb_formatSearchPredicate(
                        input = FBPredicate.predicateWithFormat(token.asString, listOf<AnyObject>()))

                    if (token is FBSelfPredicateToken) {
                        predicates.addObject(FBSelfPredicateItem(value))
                    }
                    else if (token is FBDescendantPredicateToken) {
                        predicates.addObject(FBDescendantPredicateItem(value))
                    }
                }
                else if (token is FBNumberToken) {
                    if (isPositionSet) {
                        val description: NSString = NSString("Position value '%@' is expected to be set only once.", token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }

                    val position = numberFormatter.number(token.asString)?.intValue

                    if (null == position || 0 == position) {
                        val description: NSString = NSString(
                            "Position value '%@' is expected to be a valid integer number not equal to zero.",
                            token.asString)
                        error `=` this.compilationErrorWithQuery(originalQuery, description)
                        return null
                    }

                    chainElementPosition = position
                    isPositionSet = true
                }
                else if (token is FBSplitterToken) {
                    if (!isPositionSet) {
                        chainElementPosition = 0
                    }

                    if (isDescendantSet) {
                        if (isTypeSet) {
                            result.addObject(FBClassChainItem(chainElementType, chainElementPosition, predicates.toMutableList(), true))
                            isDescendantSet = false
                        }
                    }
                    else {
                        result.addObject(
                            FBClassChainItem(chainElementType, chainElementPosition, predicates.toMutableList(), false))
                    }

                    isTypeSet = false
                    isPositionSet = false

                    predicates.removeAllObjects()
                }
            }

            if (!isPositionSet) {
                chainElementPosition = 0
            }

            if (isDescendantSet) {
                if (isTypeSet) {
                    result.addObject(FBClassChainItem(chainElementType, chainElementPosition, predicates.toMutableList(), true))
                }
                else {
                    val description: NSString = "Descendants lookup modifier '**/' should be followed with the actual element type".toNSString()
                    error `=` this.compilationErrorWithQuery(originalQuery, description)
                    return null
                }
            }
            else {
                result.addObject(
                    FBClassChainItem(chainElementType, chainElementPosition, predicates.toMutableList(), false))
            }

            return FBClassChain(result.toMutableList())
        }

        fun parseQuery(classChainQuery: NSString, error: InOut<NSError>): FBClassChain? {
            NSAssert(classChainQuery.count > 0, "Query length should be greater than zero", null)

            val tokenizedQuery: List<FBBaseClassChainToken>? = FBClassChainQueryParser.tokenizedQueryWithQuery(classChainQuery, error)

            if (null == tokenizedQuery) {
                return null
            }

            return this.compiledQueryWithTokenizedQuery(tokenizedQuery, classChainQuery, error)
        }

        open var numberFormatter: NumberFormatter = run {
            val nf: NumberFormatter = NumberFormatter()

            nf.numberStyle = NumberFormatter.Style.decimal

            nf
        }
    }
}

internal open class FBAbstractPredicateItem(value: NSPredicate): NSObject() {
    private var _value: NSPredicate
    open val value: NSPredicate
        get() {
            return _value
        }

    init {
            _value = value
    }
}

internal open class FBSelfPredicateItem(value: NSPredicate): FBAbstractPredicateItem(value) {
}

internal open class FBDescendantPredicateItem(value: NSPredicate): FBAbstractPredicateItem(value) {
}

//#import <XCTest/XCTest.h>
//#import "XCUIElement+FBClassChain.h"
//#import "FBClassChainQueryParser.h"
//#import "FBXCodeCompatibility.h"
//#import "FBExceptions.h"
internal fun XCUIElement.fb_descendantsMatchingClassChain(
    classChainQuery: NSString,
    shouldReturnAfterFirstMatch: Boolean)
    : List<XCUIElement>
{
    var error = InOut<NSError>()
    val parsedChain: FBClassChain? = FBClassChainQueryParser.parseQuery(classChainQuery, error)

    if (null == parsedChain) {
        throw NSError(
            "FBClassChainQueryParseException",
            0,
            mapOf(NSLocalizedDescriptionKey to (+error).localizedDescription))
    }

    var lookupChain = parsedChain.elements.toMutableList()
    var chainItem: FBClassChainItem = lookupChain.firstOrNull()!! // TODO: Can it throw NPE?
    var currentRoot: XCUIElement = this
    var query: XCUIElementQuery = currentRoot.fb_queryWithChainItem(item = chainItem, query = null)

    lookupChain.removeObjectAtIndex(0)

    while (lookupChain.size > 0) {
        var isRootChanged: Boolean = false

        if (0 != chainItem.position) {
            // It is necessary to resolve the query if intermediate element index is not zero or one,
            // because predicates don't support search by indexes
            val currentRootMatch: List<XCUIElement> = XCUIElement.fb_matchingElementsWithItem(
                item = chainItem,
                query = query,
                shouldReturnAfterFirstMatch = null)


            currentRoot = currentRootMatch.first ?: return listOf()
            isRootChanged = true
        }

        chainItem = lookupChain.first!!
        query = currentRoot.fb_queryWithChainItem(
            item = chainItem,
            query = if (isRootChanged) { null } else { query })

        lookupChain.removeObjectAtIndex(0)
    }

    return XCUIElement.fb_matchingElementsWithItem(chainItem, query, shouldReturnAfterFirstMatch)
}

internal fun XCUIElement.fb_queryWithChainItem(
    item: FBClassChainItem,
    query: XCUIElementQuery?)
    : XCUIElementQuery
{
    var query1 = if (item.isDescendant) {
        if ((query != null)) {
            query.descendantsMatchingType(item.type)
        } else {
            this.fb_query().descendantsMatchingType(item.type)
        }
    }
    else {
        if ((query != null)) {
            query.childrenMatchingType(item.type)
        } else {
            this.fb_query().childrenMatchingType(item.type)
        }
    }
    if ((item.predicates != null)) {
        for (predicate in item.predicates) {
            if (predicate is FBSelfPredicateItem) {
                query1 = query1.matchingPredicate(predicate.value)
            }
            else if (predicate is FBDescendantPredicateItem) {
                query1 = query1.containingPredicate(predicate.value)
            }
        }
    }
    return query1
}

internal fun XCUIElement.Companion.fb_matchingElementsWithItem(
    item: FBClassChainItem,
    query: XCUIElementQuery,
    shouldReturnAfterFirstMatch: Boolean?)
    : List<XCUIElement>
{
    if (1 == item.position || (0 == item.position && shouldReturnAfterFirstMatch!!)) {
        val result: XCUIElement? = query.fb_firstMatch()
        return if (result != null) { listOf(result) } else { listOf() }
    }

    val allMatches: List<XCUIElement> = query.fb_allMatches()

    if (0 == item.position) {
        return allMatches
    }

    if (item.position != 0) {
        if (allMatches.size >= abs(item.position!!)) {
            return if (item.position!! > 0) {
                listOf(allMatches[item.position!! - 1] as XCUIElement)
            } else {
                listOf(allMatches[allMatches.size + item.position!!] as XCUIElement)
            }
        }
    }

    return listOf()
}

//
//     Generated by class-dump 3.5 (64 bit).
//
//     class-dump is Copyright (C) 1997-1998, 2000-2001, 2004-2013 by Steve Nygard.
//
//#import <WebDriverAgentLib/CDStructures.h>
//#import <XCTest/XCUIElementQuery.h>
//#import "XCTElementSetTransformer-Protocol.h"
//#import <WebDriverAgentLib/WebDriverAgentLib.h>
//#import "XCPointerEvent.h"
//#import "FBXCodeCompatibility.h"
//#import "FBConfiguration.h"
//#import "FBErrorBuilder.h"
//#import "FBLogger.h"
//#import "XCUIApplication+FBHelpers.h"
//#import "XCUIElementQuery.h"
//#import "FBXCTestDaemonsProxy.h"
//#import "XCTestManager_ManagerInterface-Protocol.h"
internal fun XCUIElementQuery.fb_firstMatch(): XCUIElement? {
    val match: XCUIElement? = if (FBConfiguration.useFirstMatch()) { this.firstMatch() } else { this.fb_allMatches().firstOrNull() }
    return if (match?.exists() == true) { match } else { null }
}

internal fun XCUIElementQuery.fb_allMatches(): List<XCUIElement> {
    return if (FBConfiguration.boundElementsByIndex()) { this.allElementsBoundByIndex } else { this.allElementsBoundByAccessibilityElement }
}

internal val XCUIElement.fb_supportsNonModalElementsInclusion: Boolean
    get() = false

internal fun XCUIElement.fb_query(): XCUIElementQuery {
    return this.query
}

//#import <XCTest/XCTest.h>
//#import "FBClassChainQueryParser.h"
//#import "FBErrorBuilder.h"
//#import "FBElementTypeTransformer.h"
//#import "FBExceptions.h"
//#import "FBPredicate.h"
//#import "NSPredicate+FBFormat.h"
//#import "XCUIElement+FBFind.h"
//#import "FBMacros.h"
//#import "FBElementTypeTransformer.h"
//#import "FBPredicate.h"
//#import "NSPredicate+FBFormat.h"
//#import "XCElementSnapshot.h"
//#import "XCElementSnapshot+FBHelpers.h"
//#import "FBXCodeCompatibility.h"
//#import "XCUIElement+FBCaching.h"
//#import "XCUIElement+FBUtilities.h"
//#import "XCUIElement+FBWebDriverAttributes.h"
//#import "XCUIElementQuery.h"
//#import "FBElementUtils.h"
//#import "FBXCodeCompatibility.h"
//#import "FBXPath.h"
internal fun XCUIElement.fb_extractMatchingElementsFromQuery(
    query: XCUIElementQuery,
    shouldReturnAfterFirstMatch: Boolean)
    : List<XCUIElement>
{
    if (!shouldReturnAfterFirstMatch) {
        return query.fb_allMatches()
    }
    val matchedElement: XCUIElement? = query.fb_firstMatch()
    return if (matchedElement != null) { listOf(matchedElement) } else { listOf() }
}

internal fun XCUIElement.fb_cachedSnapshotWithQuery(query: XCUIElementQuery): XCElementSnapshot {
    return if (this.elementType == XCUI_ELEMENT_TYPE_APPLICATION) { query.rootElementSnapshot } else { this.fb_cachedSnapshot }
}

// MARK: - Search by ClassName
// removed

// MARK: - Search by property value
// removed

// MARK: - Search by Predicate NSString
internal fun XCUIElement.fb_descendantsMatchingPredicate(
    predicate: NSPredicate,
    shouldReturnAfterFirstMatch: Boolean)
    : List<XCUIElement>
{
    val formattedPredicate: NSPredicate = NSPredicate.fb_formatSearchPredicate(input = predicate)
    val query: XCUIElementQuery = this.fb_query().descendantsMatchingType(type = XCUIElementType.XCUI_ELEMENT_TYPE_ANY).matchingPredicate(
        predicate = formattedPredicate)
    var result = mutableListOf<XCUIElement>()

    result.addObjectsFromArray(
        this.fb_extractMatchingElementsFromQuery(query, shouldReturnAfterFirstMatch))

    val cachedSnapshot: XCElementSnapshot = this.fb_cachedSnapshotWithQuery(query = query)

    // Include self element into predicate search
    if (formattedPredicate.evaluateWithObject(cachedSnapshot)) {
        if (shouldReturnAfterFirstMatch || result.size == 0) {
            return listOf(this)
        }
        result.insertObject(this, 0)
    }

    return result.toMutableList()
}

// MARK: - Search by xpath
// removed

// MARK: - Search by Accessibility Id
// removed
