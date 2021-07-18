package com.github.tarcv.testingteam.surveyor

class LimitedClassloader(parentClassLoader: ClassLoader): ClassLoader(parentClassLoader) {
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return if (isClassAllowed(name)) {
            super.loadClass(name, resolve)
        } else {
            throw ClassNotFoundException("Not allowed")
        }
    }

    override fun findClass(name: String): Class<*> {
        return if (isClassAllowed(name)) {
            super.findClass(name)
        } else {
            throw ClassNotFoundException("Not allowed")
        }
    }

    override fun findClass(moduleName: String?, name: String): Class<*>? {
        return if (isClassAllowed(name)) {
            super.findClass(moduleName, name)
        } else {
            null
        }
    }

    private fun isClassAllowed(name: String): Boolean {
        return name.startsWith("android.") ||
                name.startsWith("androidx.")
    }
}