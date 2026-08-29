import { useState, useEffect } from 'react'

export function useDepartamentos() {
    const [departamentos, setDepartamentos] = useState<string[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const cargarDepartamentos = async () => {
            try {
                const res = await fetch('/api/departamentos')
                const data = await res.json()
                setDepartamentos(data)
            } catch (error) {
                console.error('Error al cargar departamentos:', error)
            } finally {
                setLoading(false)
            }
        }

        cargarDepartamentos()
    }, []) // Dependencia vacía: carga solo al iniciar

    return { departamentos, loading }
}