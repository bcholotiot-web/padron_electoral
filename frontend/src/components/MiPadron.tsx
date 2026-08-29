import { useEffect, useState } from 'react'
import {
    Building2,
    ClipboardList,
    Hash,
    LogOut,
    MapPin,
    TableProperties
} from 'lucide-react'
import {
    apiFetch,
    obtenerNombreUsuario
} from '../services/api'

interface PadronPersonal {
    id: number
    cui: string
    nombre: string
    apellido: string
    correo: string | null

    departamentoId: number | null
    departamento: string

    municipioId: number | null
    municipio: string

    centroVotacionId: number | null
    centroVotacion: string | null

    mesaVotacionId: number | null
    mesaVotacion: string | null

    numeroLista: number | null
    hoja: number | null
    linea: number | null
    estado: string
}

interface MiPadronProps {
    onCerrarSesion: () => void
}

export default function MiPadron({
                                     onCerrarSesion
                                 }: MiPadronProps) {
    const [padron, setPadron] =
        useState<PadronPersonal | null>(null)

    const [loading, setLoading] =
        useState(true)

    const [error, setError] =
        useState('')

    useEffect(() => {
        const cargarMiPadron = async () => {
            setLoading(true)
            setError('')

            try {
                const respuesta =
                    await apiFetch('/api/mi-padron')

                if (!respuesta.ok) {
                    const contenido =
                        await respuesta.text()

                    let mensaje =
                        'No fue posible consultar tu padrón'

                    if (contenido) {
                        try {
                            const errorBackend =
                                JSON.parse(contenido)

                            mensaje =
                                errorBackend.mensaje ??
                                errorBackend.message ??
                                mensaje
                        } catch {
                            mensaje = contenido
                        }
                    }

                    throw new Error(mensaje)
                }

                const datos: PadronPersonal =
                    await respuesta.json()

                setPadron(datos)
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : 'No fue posible cargar la información'
                )
            } finally {
                setLoading(false)
            }
        }

        cargarMiPadron()
    }, [])

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <p className="text-gray-600">
                    Cargando información electoral...
                </p>
            </div>
        )
    }

    if (error || !padron) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">
                <div className="w-full max-w-lg rounded-xl bg-white p-8 text-center shadow">
                    <p className="text-red-700">
                        {error || 'No se encontró información'}
                    </p>

                    <button
                        type="button"
                        onClick={onCerrarSesion}
                        className="mt-6 rounded-lg bg-gray-900 px-5 py-2 text-white"
                    >
                        Volver
                    </button>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-100">
            <header className="bg-white shadow-sm">
                <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-5">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">
                            Mi Padrón Electoral
                        </h1>

                        <p className="text-sm text-gray-600">
                            {obtenerNombreUsuario()}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={onCerrarSesion}
                        className="inline-flex items-center gap-2 rounded-lg bg-red-600 px-4 py-2 text-white hover:bg-red-700"
                    >
                        <LogOut size={17}/>
                        Cerrar sesión
                    </button>
                </div>
            </header>

            <main className="mx-auto max-w-5xl px-4 py-8">
                <section className="rounded-xl bg-white p-6 shadow">
                    <h2 className="text-xl font-bold text-gray-900">
                        Información personal
                    </h2>

                    <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
                        <Dato
                            titulo="Nombre completo"
                            valor={`${padron.nombre} ${padron.apellido}`}
                        />

                        <Dato
                            titulo="CUI"
                            valor={padron.cui}
                        />

                        <Dato
                            titulo="Correo"
                            valor={
                                padron.correo ||
                                'Sin correo'
                            }
                        />

                        <Dato
                            titulo="Estado"
                            valor={
                                padron.estado === 'A'
                                    ? 'Activo'
                                    : 'Inactivo'
                            }
                        />
                    </div>
                </section>

                <section className="mt-6 rounded-xl bg-white p-6 shadow">
                    <h2 className="text-xl font-bold text-gray-900">
                        Lugar de votación
                    </h2>

                    <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
                        <DatoConIcono
                            titulo="Departamento"
                            valor={padron.departamento}
                            icono={<MapPin size={20}/>}
                        />

                        <DatoConIcono
                            titulo="Municipio"
                            valor={padron.municipio}
                            icono={<MapPin size={20}/>}
                        />

                        <DatoConIcono
                            titulo="Centro de votación"
                            valor={
                                padron.centroVotacion ||
                                'Sin centro asignado'
                            }
                            icono={<Building2 size={20}/>}
                        />

                        <DatoConIcono
                            titulo="Mesa de votación"
                            valor={
                                padron.mesaVotacion ||
                                'Sin mesa asignada'
                            }
                            icono={
                                <TableProperties size={20}/>
                            }
                        />
                    </div>
                </section>

                <section className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
                    <TarjetaNumero
                        titulo="Número de lista"
                        valor={padron.numeroLista}
                        icono={<ClipboardList size={24}/>}
                    />

                    <TarjetaNumero
                        titulo="Hoja"
                        valor={padron.hoja}
                        icono={<Hash size={24}/>}
                    />

                    <TarjetaNumero
                        titulo="Línea"
                        valor={padron.linea}
                        icono={<Hash size={24}/>}
                    />
                </section>

                <div className="mt-6 rounded-lg border border-blue-200 bg-blue-50 p-4 text-blue-900">
                    Tu ubicación dentro del padrón es:
                    <strong>
                        {' '}hoja {padron.hoja ?? '-'},
                        línea {padron.linea ?? '-'}.
                    </strong>
                </div>
            </main>
        </div>
    )
}

function Dato({
                  titulo,
                  valor
              }: {
    titulo: string
    valor: string
}) {
    return (
        <div className="rounded-lg border border-gray-200 p-4">
            <p className="text-sm text-gray-500">
                {titulo}
            </p>

            <p className="mt-1 font-semibold text-gray-900">
                {valor}
            </p>
        </div>
    )
}

function DatoConIcono({
                          titulo,
                          valor,
                          icono
                      }: {
    titulo: string
    valor: string
    icono: React.ReactNode
}) {
    return (
        <div className="flex gap-3 rounded-lg border border-gray-200 p-4">
            <div className="mt-1 text-gray-700">
                {icono}
            </div>

            <div>
                <p className="text-sm text-gray-500">
                    {titulo}
                </p>

                <p className="mt-1 font-semibold text-gray-900">
                    {valor}
                </p>
            </div>
        </div>
    )
}

function TarjetaNumero({
                           titulo,
                           valor,
                           icono
                       }: {
    titulo: string
    valor: number | null
    icono: React.ReactNode
}) {
    return (
        <div className="rounded-xl bg-gray-900 p-5 text-white">
            <div className="flex items-center gap-2 text-gray-300">
                {icono}
                <span>{titulo}</span>
            </div>

            <p className="mt-3 text-3xl font-bold">
                {valor ?? '-'}
            </p>
        </div>
    )
}